package com.hjh.syt.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hjh.syt.HospitalException;
import com.hjh.syt.HttpRequestHelper;
import com.hjh.syt.ResultCodeEnum;
import com.hjh.syt.entity.order.OrderInfo;
import com.hjh.syt.entity.user.Patient;
import com.hjh.syt.enums.OrderStatusEnum;
import com.hjh.syt.hospclient.HospitalFeignClient;
import com.hjh.syt.mapper.OrderMapper;
import com.hjh.syt.rabbit.constant.MqConst;
import com.hjh.syt.rabbit.service.RabbitmqService;
import com.hjh.syt.service.OrderService;
import com.hjh.syt.userclient.PatientFeignClient;
import com.hjh.syt.vo.eml.EmailVo;
import com.hjh.syt.vo.hosp.ScheduleOrderVo;
import com.hjh.syt.vo.order.*;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl extends
        ServiceImpl<OrderMapper, OrderInfo> implements OrderService {

    @Resource
    private PatientFeignClient patientFeignClient;

    @Resource
    private HospitalFeignClient hospitalFeignClient;

    @Resource
    private RabbitmqService rabbitService;


    //生成挂号订单
    @Override
    public Long saveOrder(String scheduleId, Long patientId) {
        //获取就诊人信息
        Patient patient = patientFeignClient.getPatientOrder(patientId);

        //获取排班相关信息
        ScheduleOrderVo scheduleOrderVo = hospitalFeignClient.getScheduleOrderVo(scheduleId);

        //判断当前时间是否还可以预约
        if(DateUtil.compare(new DateTime(scheduleOrderVo.getStartTime()),new DateTime())>0||
                DateUtil.compare(new DateTime(scheduleOrderVo.getEndTime()),new DateTime())<0) {
            throw new HospitalException(ResultCodeEnum.TIME_NO);
        }

        //获取签名信息
        SignInfoVo signInfoVo = hospitalFeignClient.getSignInfoVo(scheduleOrderVo.getHoscode());

        //添加到订单表
        OrderInfo orderInfo = new OrderInfo();
        //scheduleOrderVo 数据复制到 orderInfo
        BeanUtils.copyProperties(scheduleOrderVo,orderInfo);
        //向orderInfo设置其他数据
        //订单编号
        String outTradeNo = System.currentTimeMillis() + ""+ new Random().nextInt(100);
        orderInfo.setOutTradeNo(outTradeNo);
        orderInfo.setScheduleId(scheduleId);
        orderInfo.setUserId(patient.getUserId());
        orderInfo.setPatientId(patientId);
        orderInfo.setPatientName(patient.getName());
        orderInfo.setPatientPhone(patient.getPhone());
        orderInfo.setOrderStatus(OrderStatusEnum.UNPAID.getStatus());
        baseMapper.insert(orderInfo);

        //调用医院接口，实现预约挂号操作
        //设置调用医院接口需要参数，参数放到map集合
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("hoscode",orderInfo.getHoscode());
        paramMap.put("depcode",orderInfo.getDepcode());
        paramMap.put("hosScheduleId",orderInfo.getScheduleId());
        paramMap.put("reserveDate",new DateTime(orderInfo.getReserveDate()).toString("yyyy-MM-dd"));
        paramMap.put("reserveTime", orderInfo.getReserveTime());
        paramMap.put("amount",orderInfo.getAmount());

        paramMap.put("name", patient.getName());
        paramMap.put("certificatesType",patient.getCertificatesType());
        paramMap.put("certificatesNo", patient.getCertificatesNo());
        paramMap.put("sex",patient.getSex());
        paramMap.put("birthdate", patient.getBirthdate());
        paramMap.put("phone",patient.getPhone());
        paramMap.put("isMarry", patient.getIsMarry());
        paramMap.put("provinceCode",patient.getProvinceCode());
        paramMap.put("cityCode", patient.getCityCode());
        paramMap.put("districtCode",patient.getDistrictCode());
        paramMap.put("address",patient.getAddress());
        //联系人
        paramMap.put("contactsName",patient.getContactsName());
        paramMap.put("contactsCertificatesType", patient.getContactsCertificatesType());
        paramMap.put("contactsCertificatesNo",patient.getContactsCertificatesNo());
        paramMap.put("contactsPhone",patient.getContactsPhone());
        paramMap.put("timestamp", DateUtil.current());
        paramMap.put("sign", signInfoVo.getSignKey());
        //请求医院系统接口
        JSONObject result = HttpRequestHelper.sendRequest(paramMap, signInfoVo.getApiUrl() + "/order/submitOrder");

        if(result.getInteger("code")==200) {
            JSONObject jsonObject = result.getJSONObject("data");
            //预约记录唯一标识（医院预约记录主键）
            String hosRecordId = jsonObject.getString("hosRecordId");
            //预约序号
            Integer number = jsonObject.getInteger("number");;
            //取号时间
            String fetchTime = jsonObject.getString("fetchTime");;
            //取号地址
            String fetchAddress = jsonObject.getString("fetchAddress");;
            //更新订单
            orderInfo.setHosRecordId(hosRecordId);
            orderInfo.setNumber(number);
            orderInfo.setFetchTime(fetchTime);
            orderInfo.setFetchAddress(fetchAddress);
            baseMapper.updateById(orderInfo);
            //排班可预约数
            Integer reservedNumber = jsonObject.getInteger("reservedNumber");
            //排班剩余预约数
            Integer availableNumber = jsonObject.getInteger("availableNumber");
            //发送mq消息，号源更新和短信通知
            //发送mq信息更新号源
            OrderMqVo orderMqVo = new OrderMqVo();
            orderMqVo.setScheduleId(scheduleId);
            orderMqVo.setReservedNumber(reservedNumber);
            orderMqVo.setAvailableNumber(availableNumber);
            //短信提示
            EmailVo msmVo = new EmailVo();
            msmVo.setMailNumber(orderInfo.getPatientPhone());
            String reserveDate = new DateTime(orderInfo.getReserveDate()).toString("yyyy-MM-dd") + (orderInfo.getReserveTime()==0 ? "上午" : "下午");
            Map<String,Object> param = new HashMap<String,Object>(){{
                put("title", orderInfo.getHosname()+"|"+orderInfo.getDepname()+"|"+orderInfo.getTitle());
                put("amount", orderInfo.getAmount());
                put("reserveDate", reserveDate);
                put("name", orderInfo.getPatientName());
                put("quitTime", new DateTime(orderInfo.getQuitTime()).toString("yyyy-MM-dd HH:mm"));
            }};
            msmVo.setParam(param);
            orderMqVo.setMsmVo(msmVo);

            rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_ORDER, MqConst.ROUTING_ORDER, orderMqVo);
        } else {
            throw new HospitalException(result.getString("message"), ResultCodeEnum.FALL.getCode());
        }
        return orderInfo.getId();
    }

    //根据订单id查询订单详情
    @Override
    public OrderInfo getOrder(String orderId) {
        OrderInfo orderInfo = baseMapper.selectById(orderId);
        return this.packOrderInfo(orderInfo);
    }

    //订单列表（条件查询带分页）
    @Override
    public IPage<OrderInfo> selectPage(Page<OrderInfo> pageParam, OrderQueryVo orderQueryVo) {
        //orderQueryVo获取条件值
        String name = orderQueryVo.getKeyword(); //医院名称
        Long patientId = orderQueryVo.getPatientId(); //就诊人名称
        String orderStatus = orderQueryVo.getOrderStatus(); //订单状态
        String reserveDate = orderQueryVo.getReserveDate();//安排时间
        String createTimeBegin = orderQueryVo.getCreateTimeBegin();
        String createTimeEnd = orderQueryVo.getCreateTimeEnd();

        //对条件值进行非空判断
        QueryWrapper<OrderInfo> wrapper = new QueryWrapper<>();
        if(!StringUtils.isEmpty(name)) {
            wrapper.like("hosname",name);
        }
        if(!StringUtils.isEmpty(patientId)) {
            wrapper.eq("patient_id",patientId);
        }
        if(!StringUtils.isEmpty(orderStatus)) {
            wrapper.eq("order_status",orderStatus);
        }
        if(!StringUtils.isEmpty(reserveDate)) {
            wrapper.ge("reserve_date",reserveDate);
        }
        if(!StringUtils.isEmpty(createTimeBegin)) {
            wrapper.ge("create_time",createTimeBegin);
        }
        if(!StringUtils.isEmpty(createTimeEnd)) {
            wrapper.le("create_time",createTimeEnd);
        }
        //调用mapper的方法
        IPage<OrderInfo> pages = baseMapper.selectPage(pageParam, wrapper);
        //编号变成对应值封装
        pages.getRecords().stream().forEach(item -> {
            this.packOrderInfo(item);
        });
        return pages;
    }

    //取消预约
    @Override
    public Boolean cancelOrder(Long orderId) {
        //获取订单信息
        OrderInfo orderInfo = baseMapper.selectById(orderId);
        //判断是否取消
        //退号时间-规定时间内都可以退
        DateTime quitTime = new DateTime(orderInfo.getQuitTime());
        //判断是否已经过了退号时间
        if(DateUtil.compare(quitTime,new DateTime())<0) {
            throw new HospitalException(ResultCodeEnum.CANCEL_ORDER_NO);
        }
        //调用医院接口实现预约取消
        //根据医院编号获取签名
        SignInfoVo signInfoVo = hospitalFeignClient.getSignInfoVo(orderInfo.getHoscode());
        //判断签名是否为空
        if(null == signInfoVo) {
            throw new HospitalException(ResultCodeEnum.PARAM_ERROR);
        }
        Map<String, Object> reqMap = new HashMap<>();
        //医院编号
        reqMap.put("hoscode",orderInfo.getHoscode());
        //订单编号
        reqMap.put("hosRecordId",orderInfo.getHosRecordId());
        //取消订单请求的当前时间戳
        reqMap.put("timestamp", DateUtil.current());
        //签名
        reqMap.put("sign", signInfoVo.getSignKey());
        //发送请求
        JSONObject result = HttpRequestHelper.sendRequest(reqMap,
                signInfoVo.getApiUrl()+"/order/updateCancelStatus");
        //根据医院接口返回数据
        if(result.getInteger("code")!=200) {
            throw new HospitalException(result.getString("message"), ResultCodeEnum.FALL.getCode());
        } else {
            //判断当前订单是否可以取消
            if(orderInfo.getOrderStatus().intValue() == OrderStatusEnum.PAID.getStatus().intValue()||
                    orderInfo.getOrderStatus().intValue() == OrderStatusEnum.UNPAID.getStatus().intValue()) {
                //判断是否退款成功

                //更新订单状态
                orderInfo.setOrderStatus(OrderStatusEnum.CANCLE.getStatus());
                baseMapper.updateById(orderInfo);

                //发送mq更新预约数量
                OrderMqVo orderMqVo = new OrderMqVo();
                //设置排班id
                orderMqVo.setScheduleId(orderInfo.getScheduleId());
                //邮件提示
                EmailVo msmVo = new EmailVo();
                //邮件
                msmVo.setMailNumber(orderInfo.getPatientPhone());
                String reserveDate = new DateTime(orderInfo.getReserveDate()).toString("yyyy-MM-dd") + (orderInfo.getReserveTime()==0 ? "上午": "下午");
                Map<String,Object> param = new HashMap<String,Object>(){{
                    put("title", orderInfo.getHosname()+"|"+orderInfo.getDepname()+"|"+orderInfo.getTitle());
                    put("reserveDate", reserveDate);
                    put("name", orderInfo.getPatientName());
                }};
                msmVo.setParam(param);
                orderMqVo.setMsmVo(msmVo);
                rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_ORDER, MqConst.ROUTING_ORDER, orderMqVo);
            }
        }
        return true;
    }

    //就诊通知
    @Override
    public void patientTips() {
        QueryWrapper<OrderInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("reserve_date",new DateTime().toString("yyyy-MM-dd"));
        wrapper.ne("order_status",OrderStatusEnum.CANCLE.getStatus());
        List<OrderInfo> orderInfoList = baseMapper.selectList(wrapper);
        for(OrderInfo orderInfo:orderInfoList) {
            //短信提示
            EmailVo msmVo = new EmailVo();
            msmVo.setMailNumber(orderInfo.getPatientPhone());
            String reserveDate = new DateTime(orderInfo.getReserveDate()).toString("yyyy-MM-dd") + (orderInfo.getReserveTime()==0 ? "上午": "下午");
            Map<String,Object> param = new HashMap<String,Object>(){{
                put("title", orderInfo.getHosname()+"|"+orderInfo.getDepname()+"|"+orderInfo.getTitle());
                put("reserveDate", reserveDate);
                put("name", orderInfo.getPatientName());
            }};
            msmVo.setParam(param);
            rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_EMAIL, MqConst.ROUTING_EMAIL, msmVo);
        }
    }

    //预约统计
    @Override
    public Map<String, Object> getCountMap(OrderCountQueryVo orderCountQueryVo) {
        //调用mapper方法得到数据
        List<OrderCountVo> orderCountVoList = baseMapper.selectOrderCount(orderCountQueryVo);

        //获取x需要数据 ，日期数据  list集合
        List<String> dateList = orderCountVoList.stream().map(OrderCountVo::getReserveDate).collect(Collectors.toList());

        //获取y需要数据，具体数量  list集合
        List<Integer> countList =orderCountVoList.stream().map(OrderCountVo::getCount).collect(Collectors.toList());

        Map<String,Object> map = new HashMap<>();
        map.put("dateList",dateList);
        map.put("countList",countList);
        return map;
    }

    private OrderInfo packOrderInfo(OrderInfo orderInfo) {
        orderInfo.getParam().put("orderStatusString", OrderStatusEnum.getStatusNameByStatus(orderInfo.getOrderStatus()));
        return orderInfo;
    }

}
