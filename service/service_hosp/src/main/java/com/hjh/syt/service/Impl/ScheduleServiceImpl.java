package com.hjh.syt.service.Impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hjh.syt.HospitalException;
import com.hjh.syt.ResultCodeEnum;
import com.hjh.syt.entity.hosp.BookingRule;
import com.hjh.syt.entity.hosp.Department;
import com.hjh.syt.entity.hosp.Hospital;
import com.hjh.syt.entity.hosp.Schedule;
import com.hjh.syt.repository.ScheduleRepository;
import com.hjh.syt.service.DepartmentService;
import com.hjh.syt.service.HospitalService;
import com.hjh.syt.service.ScheduleService;
import com.hjh.syt.vo.hosp.BookingScheduleRuleVo;
import com.hjh.syt.vo.hosp.ScheduleOrderVo;
import com.hjh.syt.vo.hosp.ScheduleQueryVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: hasee
 * @date: 2021/7/2 21:01
 * @description:
 */

@Service
@Slf4j
public class ScheduleServiceImpl implements ScheduleService {

    @Resource
    private ScheduleRepository scheduleRepository;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Resource
    private HospitalService hospitalService;
    @Resource
    private DepartmentService departmentService;

    @Override
    public void saveSchedule(Map<String, Object> map) {
        String jsonString = JSONObject.toJSONString(map);
        Schedule schedule = JSONObject.parseObject(jsonString, Schedule.class);
        //根据医院编号和排班编号查询
        Schedule scheduleExist = scheduleRepository
                .findScheduleByHospCodeAndHosScheduleId(schedule.getHospCode(),schedule.getHosScheduleId());
        if (scheduleExist != null){
            scheduleExist.setUpdateTime(new Date());
            scheduleExist.setIsDeleted(0);
            scheduleRepository.save(scheduleExist);
        }else {
            schedule.setCreateTime(new Date());
            schedule.setUpdateTime(new Date());
            schedule.setIsDeleted(0);
            scheduleRepository.save(schedule);
        }
    }

    @Override
    public Page<Schedule> findPageSchedule(Integer page, Integer limit, ScheduleQueryVo scheduleQueryVo) {

        Pageable pageable = PageRequest.of(page-1,limit);
        //设置Schedule
        Schedule schedule = new Schedule();
        BeanUtils.copyProperties(scheduleQueryVo,schedule);
        schedule.setIsDeleted(0);
        //查询规则
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING) //模糊规则
                .withIgnoreCase(true); //忽略大小写
        //查询对象
        Example example = Example.of(schedule,exampleMatcher);
        return scheduleRepository.findAll(example,pageable);
    }

    @Override
    public void remove(String hospCode, String hosScheduleId) {
        Schedule schedule = scheduleRepository
                .findScheduleByHospCodeAndHosScheduleId(hospCode, hosScheduleId);
        if (schedule != null){
            scheduleRepository.deleteById(schedule.getId());
        }
    }
    //根据医院编号 和 科室编号 ，查询排班规则数据
    @Override
    public Map<String, Object> getRuleSchedule(long page, long limit, String hoscode, String depcode) {
        log.info(hoscode+depcode);
        //1 根据医院编号 和 科室编号 查询
        Criteria criteria = Criteria.where("hospCode").is(hoscode).and("depCode").is(depcode);
        //2 根据工作日workDate期进行分组
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.match(criteria),//匹配条件
                Aggregation.group("workDate")//分组字段
                        .first("workDate").as("workDate")
                        //3 统计号源数量
                        .count().as("docCount")
                        .sum("reservedNumber").as("reservedNumber")
                        .sum("availableNumber").as("availableNumber"),
                //排序
                Aggregation.sort(Sort.Direction.DESC,"workDate"),
                //4 实现分页
                Aggregation.skip((page-1)*limit),
                Aggregation.limit(limit)
        );
        //调用方法，最终执行
        AggregationResults<BookingScheduleRuleVo> aggResults =
                mongoTemplate.aggregate(agg, Schedule.class, BookingScheduleRuleVo.class);
        List<BookingScheduleRuleVo> bookingScheduleRuleVoList = aggResults.getMappedResults();
        log.info("bookingScheduleRuleVoList"+bookingScheduleRuleVoList);
        //分组查询的总记录数
        Aggregation totalAgg = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group("workDate")
        );
        AggregationResults<BookingScheduleRuleVo> totalAggResults =
                mongoTemplate.aggregate(totalAgg, Schedule.class, BookingScheduleRuleVo.class);
        int total = totalAggResults.getMappedResults().size();
        //把日期对应星期获取
        for(BookingScheduleRuleVo bookingScheduleRuleVo:bookingScheduleRuleVoList) {
            Date workDate = bookingScheduleRuleVo.getWorkDate();
            String dayOfWeek = this.getDayOfWeek(new DateTime(workDate));
            bookingScheduleRuleVo.setDayOfWeek(dayOfWeek);
        }

        //设置最终数据，进行返回
        Map<String, Object> result = new HashMap<>();
        result.put("bookingScheduleRuleList",bookingScheduleRuleVoList);
        result.put("total",total);
        //获取医院名称
        String hosName = hospitalService.getHospName(hoscode);
        //其他基础数据
        Map<String, String> baseMap = new HashMap<>();
        baseMap.put("hosname",hosName);
        result.put("baseMap",baseMap);
        log.info("最终结果"+result);
        return result;
    }

    //根据医院编号 、科室编号和工作日期，查询排班详细信息
    @Override
    public List<Schedule> getDetailSchedule(String hoscode, String depcode, String workDate) {
        //根据参数查询mongodb
        List<Schedule> scheduleList =
                scheduleRepository.findScheduleByHospCodeAndDepCodeAndWorkDate(hoscode,depcode,new DateTime(workDate));
        //把得到list集合遍历，向设置其他值：医院名称、科室名称、日期对应星期
        scheduleList.stream().forEach(item->{
            this.packageSchedule(item);
        });
        return scheduleList;
    }

    //获取可预约的排班数据
    @Override
    public Map<String, Object> getBookingScheduleRule(int page, int limit, String hoscode, String depcode) {
        Map<String,Object> result = new HashMap<>();
        //获取预约规则
        //根据医院编号获取预约规则
        Hospital hospital = hospitalService.getByHospital(hoscode);
        if(hospital == null) {
            throw new HospitalException(ResultCodeEnum.DATA_ERROR);
        }
        BookingRule bookingRule = hospital.getBookingRule();

        //获取可预约日期的数据（分页）
        IPage iPage = this.getListDate(page,limit,bookingRule);
        //当前可预约日期
        List<Date> dateList = iPage.getRecords();

        //获取可预约日期里面科室的剩余预约数
        Criteria criteria = Criteria.where("hospCode").is(hoscode).and("depCode").is(depcode)
                .and("workDate").in(dateList);

        Aggregation agg = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group("workDate").first("workDate").as("workDate")
                        .count().as("docCount")
                        .sum("availableNumber").as("availableNumber")
                        .sum("reservedNumber").as("reservedNumber")
        );
        AggregationResults<BookingScheduleRuleVo> aggregateResult =
                mongoTemplate.aggregate(agg, Schedule.class, BookingScheduleRuleVo.class);
        List<BookingScheduleRuleVo> scheduleVoList = aggregateResult.getMappedResults();

        //合并数据  map集合 key日期  value预约规则和剩余数量等
        Map<Date, BookingScheduleRuleVo> scheduleVoMap = new HashMap<>();
        if(!CollectionUtils.isEmpty(scheduleVoList)) {
            scheduleVoMap = scheduleVoList.stream().
                    collect(
                            Collectors.toMap(BookingScheduleRuleVo::getWorkDate,
                                    BookingScheduleRuleVo -> BookingScheduleRuleVo));
        }

        //获取可预约排班规则
        List<BookingScheduleRuleVo> bookingScheduleRuleVoList = new ArrayList<>();
        for(int i=0,len=dateList.size();i<len;i++) {
            Date date = dateList.get(i);
            //从map集合根据key日期获取value值
            BookingScheduleRuleVo bookingScheduleRuleVo = scheduleVoMap.get(date);
            //如果当天没有排班医生
            if(bookingScheduleRuleVo == null) {
                bookingScheduleRuleVo = new BookingScheduleRuleVo();
                //就诊医生人数
                bookingScheduleRuleVo.setDocCount(0);
                //科室剩余预约数  -1表示无号
                bookingScheduleRuleVo.setAvailableNumber(-1);
            }
            bookingScheduleRuleVo.setWorkDate(date);
            bookingScheduleRuleVo.setWorkDateMd(date);
            //计算当前预约日期对应星期
            String dayOfWeek = this.getDayOfWeek(new DateTime(date));
            bookingScheduleRuleVo.setDayOfWeek(dayOfWeek);

            //最后一页最后一条记录为即将预约   状态 0：正常 1：即将放号 -1：当天已停止挂号
            if(i == len-1 && page == iPage.getPages()) {
                bookingScheduleRuleVo.setStatus(1);
            } else {
                bookingScheduleRuleVo.setStatus(0);
            }
            //当天预约如果过了停号时间， 不能预约
            if(i == 0 && page == 1) {
                DateTime stopTime = this.getDateTime(new Date(), bookingRule.getStopTime());
                if(DateUtil.compare(stopTime,new DateTime())<0) {
                    //停止预约
                    bookingScheduleRuleVo.setStatus(-1);
                }
            }
            bookingScheduleRuleVoList.add(bookingScheduleRuleVo);
        }

        //可预约日期规则数据
        result.put("bookingScheduleList", bookingScheduleRuleVoList);
        result.put("total", iPage.getTotal());

        //其他基础数据
        Map<String, String> baseMap = new HashMap<>();
        //医院名称
        baseMap.put("hosname", hospitalService.getHospName(hoscode));
        //科室
        Department department =departmentService.getDepartment(hoscode, depcode);
        //大科室名称
        baseMap.put("bigname", department.getBigName());
        //科室名称
        baseMap.put("depname", department.getDepName());
//月
        baseMap.put("workDateString", new DateTime().toString("yyyy年MM月"));
//放号时间
        baseMap.put("releaseTime", bookingRule.getReleaseTime());
//停号时间
        baseMap.put("stopTime", bookingRule.getStopTime());
        result.put("baseMap", baseMap);
        return result;
    }

    ////获取排班id获取排班数据
    @Override
    public Schedule getScheduleId(String scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId).get();
        return this.packageSchedule(schedule);
    }

    //根据排班id获取预约下单数据
    @Override
    public ScheduleOrderVo getScheduleOrderVo(String scheduleId) {
        ScheduleOrderVo scheduleOrderVo = new ScheduleOrderVo();
        //获取排班信息
        Schedule schedule = this.getScheduleId(scheduleId);
        //Schedule schedule = baseMapper.selectById(scheduleId);
        if(schedule == null) {
            throw new HospitalException(ResultCodeEnum.PARAM_ERROR);
        }
        //获取预约规则信息
        Hospital hospital = hospitalService.getByHospital(schedule.getHospCode());
        if(hospital == null) {
            throw new HospitalException(ResultCodeEnum.PARAM_ERROR);
        }
        BookingRule bookingRule = hospital.getBookingRule();
        if(bookingRule == null) {
            throw new HospitalException(ResultCodeEnum.PARAM_ERROR);
        }

        //把获取数据设置到scheduleOrderVo
        scheduleOrderVo.setHoscode(schedule.getHospCode());
        scheduleOrderVo.setHosname(hospitalService.getHospName(schedule.getHospCode()));
        scheduleOrderVo.setDepcode(schedule.getDepCode());
        scheduleOrderVo.setDepname(departmentService.getDepName(schedule.getHospCode(), schedule.getDepCode()));
        scheduleOrderVo.setHosScheduleId(schedule.getHosScheduleId());
        scheduleOrderVo.setAvailableNumber(schedule.getAvailableNumber());
        scheduleOrderVo.setTitle(schedule.getTitle());
        scheduleOrderVo.setReserveDate(schedule.getWorkDate());
        scheduleOrderVo.setReserveTime(schedule.getWorkTime());
        scheduleOrderVo.setAmount(schedule.getAmount());

        //退号截止天数（如：就诊前一天为-1，当天为0）
        int quitDay = bookingRule.getQuitDay();
        DateTime quitTime = this.getDateTime(DateUtil.offsetDay(new Date(),quitDay).toJdkDate(), bookingRule.getQuitTime());
        scheduleOrderVo.setQuitTime(quitTime.toJdkDate());

        //预约开始时间
        DateTime startTime = this.getDateTime(new Date(), bookingRule.getReleaseTime());
        scheduleOrderVo.setStartTime(startTime.toJdkDate());

        //预约截止时间
        DateTime endTime = this.getDateTime(DateUtil.offsetDay(new Date(),bookingRule.getCycle()).toJdkDate(), bookingRule.getStopTime());
        scheduleOrderVo.setEndTime(endTime.toJdkDate());

        //当天停止挂号时间
        DateTime stopTime = this.getDateTime(new Date(), bookingRule.getStopTime());
        scheduleOrderVo.setStartTime(startTime.toJdkDate());
        return scheduleOrderVo;
    }

    //更新排班信息 用于mp
    @Override
    public void update(Schedule schedule) {
        schedule.setUpdateTime(new Date());
        scheduleRepository.save(schedule);
    }

    //获取可预约日志分页数据
    private IPage getListDate(int page, int limit, BookingRule bookingRule) {
        //获取当天放号时间  年 月 日 小时 分钟
        DateTime releaseTime = this.getDateTime(new Date(), bookingRule.getReleaseTime());
        //获取预约周期
        Integer cycle = bookingRule.getCycle();
        //如果当天放号时间已经过去了，预约周期从后一天开始计算，周期+1
        if(DateUtil.compare(releaseTime,new DateTime())<0) {
            cycle += 1;
        }
        //获取可预约所有日期，最后一天显示即将放号
        List<Date> dateList = new ArrayList<>();
        for(int i=0;i<cycle;i++) {
            DateTime curDateTime = DateUtil.offsetDay(new Date(),i);
            String dateString = curDateTime.toString("yyyy-MM-dd");
            dateList.add(new DateTime(dateString).toSqlDate());
        }
        //因为预约周期不同的，每页显示日期最多7天数据，超过7天分页
        List<Date> pageDateList = new ArrayList<>();
        int start = (page-1)*limit;
        int end = (page-1)*limit+limit;
        //如果可以显示数据小于7，直接显示
        if(end > dateList.size()) {
            end = dateList.size();
        }
        for(int i=start;i<end;i++) {
            pageDateList.add(dateList.get(i));
        }
        //如果可以显示数据大于7，进行分页
        IPage<Date> iPage = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page,7,dateList.size());
        iPage.setRecords(pageDateList);
        return iPage;
    }

    /**
     * 将Date日期（yyyy-MM-dd HH:mm）转换为DateTime
     */
    private DateTime getDateTime(Date date, String timeString) {
        String dateTimeString = new DateTime(date).toString("yyyy-MM-dd") + " "+ timeString;
        return DateUtil.parse(dateTimeString);
    }

    //封装排班详情其他值 医院名称、科室名称、日期对应星期
    private Schedule packageSchedule(Schedule schedule) {
        //设置医院名称
        schedule.getParam().put("hosname",hospitalService.getHospName(schedule.getHospCode()));
        //设置科室名称
        schedule.getParam().put("depname",departmentService.getDepName(schedule.getHospCode(),schedule.getDepCode()));
        //设置日期对应星期
        schedule.getParam().put("dayOfWeek",this.getDayOfWeek(new DateTime(schedule.getWorkDate())));
        return schedule;
    }

    /**
     * 根据日期获取周几数据
     * @param dateTime
     * @return
     */
    private String getDayOfWeek(DateTime dateTime) {
        String dayOfWeek = "";
        switch (dateTime.dayOfWeek()) {
            case 1:
                dayOfWeek = "周日";
                break;
            case 2:
                dayOfWeek = "周一";
                break;
            case 3:
                dayOfWeek = "周二";
                break;
            case 4:
                dayOfWeek = "周三";
                break;
            case 5:
                dayOfWeek = "周四";
                break;
            case 6:
                dayOfWeek = "周五";
                break;
            case 7:
                dayOfWeek = "周六";
            default:
                break;
        }
        return dayOfWeek;
    }

}
