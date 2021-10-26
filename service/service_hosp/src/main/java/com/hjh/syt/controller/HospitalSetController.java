package com.hjh.syt.controller;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hjh.syt.Result;
import com.hjh.syt.ResultCodeEnum;
import com.hjh.syt.entity.hosp.HospitalSet;
import com.hjh.syt.rabbit.constant.MqConst;
import com.hjh.syt.rabbit.service.RabbitmqService;
import com.hjh.syt.service.HospitalService;
import com.hjh.syt.service.HospitalSetService;
import com.hjh.syt.vo.eml.EmailVo;
import com.hjh.syt.vo.hosp.HospitalSetQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author: hasee
 * @date: 2021/6/14 23:32
 * @description:
 */
@RestController
@Slf4j
@RequestMapping("/admin/hosp/hospitalSet")
@Api(tags = "管理入驻医院")
//@CrossOrigin //允许跨界访问
public class HospitalSetController {

    @Value("${serverPort.url}")
    private String url;
    @Value("${url}")
    private String serverURL;
    @Resource
    private HospitalSetService hospitalSetService;
    @Resource
    private RabbitmqService rabbitmqService;
    @Resource
    private HospitalService hospitalService;

    @ApiOperation(value = "获取所有入驻医院信息")
    @GetMapping("/findAll")
    public Result findAllHospitalSet(){
        log.info("开始查询");
        return Result.builds(hospitalSetService.list(), ResultCodeEnum.SUCCESS);
    }

    @ApiOperation(value = "逻辑删除入驻医院信息")
    @DeleteMapping("/Delete/{id}")
    public Result DeleteByIdHospitalSet(@PathVariable("id") Long id){
        log.info("开始删除");
        if(hospitalSetService.removeById(id)){
            return Result.builds(ResultCodeEnum.SUCCESS);
        }
        return Result.builds(ResultCodeEnum.FALL);
    }

    @ApiOperation(value = "可以加条件的分页查询入驻医院信息")
    @PostMapping("/PagingSelect/{current}/{limit}")
    public Result PagingSelectHospitalSet(@PathVariable("current") long current,
                                          @PathVariable("limit") long limit,
                                          @RequestBody(required = false) HospitalSetQueryVo hospitalSetQueryVo){
        log.info("开始分页查询");
        //创建page对象，传递当前页，每页记录数
        String hospName = hospitalSetQueryVo.getHospName();
        String hospCode = hospitalSetQueryVo.getHospCode();
        Page<HospitalSet> page = new Page<>(current,limit);
        QueryWrapper queryWrapper = new QueryWrapper();
        if (!StringUtils.isEmpty(hospName)){
            queryWrapper.like("hosp_name",hospName);
        }
        if (!StringUtils.isEmpty(hospCode)){
            queryWrapper.eq("hosp_code",hospCode);
        }
        return Result.builds(hospitalSetService.page(page, queryWrapper),ResultCodeEnum.SUCCESS);
    }

    @ApiOperation(value = "添加入驻医院信息")
    @PostMapping("/Add")
    public Result AddHospitalSet(@RequestBody HospitalSet hospitalSet){
            hospitalSet.setStatus(0); //状态：1为可用，0为不可用
            hospitalSet.setApiUrl(serverURL);
            hospitalSet.setSignKey(SecureUtil.md5().digestHex(new Date()
                    + " "+ RandomUtil.randomNumbers(10)));
            log.info("医院信息："+hospitalSet);
            EmailVo emailVo = this.getMail(hospitalSet);
            log.info("开始添加");
            if (hospitalSetService.save(hospitalSet)){
                rabbitmqService.sendMessage(MqConst.EXCHANGE_DIRECT_ADD,MqConst.ROUTING_ADD,emailVo);
                return Result.builds(ResultCodeEnum.SUCCESS);
            }
        return Result.builds(ResultCodeEnum.FALL);
    }

    @ApiOperation(value = "根据id获取入驻医院信息")
    @GetMapping("/get/{id}")
    public Result getByIdHospitalSet(@PathVariable("id") Long id){
        return Result.builds(hospitalSetService.getById(id),ResultCodeEnum.SUCCESS);
    }

    @ApiOperation(value = "修改入驻医院信息")
    @PostMapping("/update")
    public Result UpdateHospitalSet(@RequestBody HospitalSet hospitalSet){
        log.info("开始修改");
        boolean update = hospitalSetService.updateById(hospitalSet);
        if (update){
            return Result.builds(ResultCodeEnum.SUCCESS);
        }
        return Result.builds(ResultCodeEnum.FALL);
    }

    @ApiOperation(value = "批量删除医院信息")
    @DeleteMapping("/deletes")
    public Result DeletesHospitalSet(@RequestBody List<Long> listId){
        log.info("开始批量删除");
        boolean save = hospitalSetService.removeByIds(listId);
        if (save){
            return Result.builds(ResultCodeEnum.SUCCESS);
        }
        return Result.builds(ResultCodeEnum.FALL);
    }

    @ApiOperation(value = "锁定和解锁医院信息")
    @PutMapping("/lockHospital/{id}/{status}")
    public Result lockHospitalSet(@PathVariable("id") Long id
            ,@PathVariable("status") Integer status){
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        if (hospitalService.getByHospital(hospitalSet.getHospCode()).getStatus()!=1){
            hospitalSet.setStatus(status);
            EmailVo emailVo = this.getMail(hospitalSet);
            log.info("开始锁定指定医院信息:"+hospitalSet);
            hospitalSetService.updateById(hospitalSet);
            rabbitmqService.sendMessage(MqConst.EXCHANGE_DIRECT_ADD,MqConst.ROUTING_ADD,emailVo);
            return Result.builds(ResultCodeEnum.SUCCESS);
        }
        return Result.builds(ResultCodeEnum.FALL);
    }
    @ApiOperation(value = "签名")
    @PutMapping("/lockHospital/{id}")
    public Result lockHospitalSet(@PathVariable("id") Long id){
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        String key = hospitalSet.getSignKey();
        String hospCode = hospitalSet.getHospCode();
        //发送短信
        return Result.builds(ResultCodeEnum.SUCCESS);
    }

    private EmailVo getMail(HospitalSet hospitalSet) {
        EmailVo emailVo = new EmailVo();
        emailVo.setMailNumber(hospitalSet.getContactsPhone());
        Map<String,Object> param = new HashMap<>();
        param.put("code",RandomUtil.randomNumbers(10));
        param.put("toUrl",hospitalSet.getApiUrl());
        param.put("hospCode",hospitalSet.getHospCode());
        param.put("aipUrl",url);
        param.put("SignKey",hospitalSet.getSignKey());
        emailVo.setParam(param);
        return emailVo;
    }
}
