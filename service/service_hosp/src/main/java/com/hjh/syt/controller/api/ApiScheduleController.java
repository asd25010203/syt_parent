package com.hjh.syt.controller.api;

import com.baomidou.mybatisplus.extension.api.R;
import com.hjh.syt.HospitalException;
import com.hjh.syt.Result;
import com.hjh.syt.ResultCodeEnum;
import com.hjh.syt.service.HospitalSetService;
import com.hjh.syt.service.ScheduleService;
import com.hjh.syt.vo.hosp.ScheduleQueryVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author: hasee
 * @date: 2021/7/2 20:53
 * @description:
 */

@RestController
@Slf4j
@RequestMapping("/api/Schedule")
public class ApiScheduleController {

    @Resource
    private ScheduleService scheduleService;

//    @Resource
//    private HospitalSetService hospitalSetService;

    //上传排班
    @PostMapping("/saveSchedule")
    public Result saveSchedule(@RequestParam Map<String, Object> map) {
        //获取医院编号
//        String hospCode = (String)map.get("hospCode");
//        //获取签名
//        String sign = (String)map.get("sign");
//        //校验签名
//        if (!hospitalSetService.checkSign(hospCode,sign)){
//            throw new HospitalException(ResultCodeEnum.SIGN_ERROR);
//        }else {
        scheduleService.saveSchedule(map);
        return Result.builds(ResultCodeEnum.SUCCESS);
//        }
    }

    //查询排班
    @PostMapping("/list")
    public Result PageSchedule(@RequestParam Map<String, Object> map) {
        Integer page = StringUtils.isEmpty(map.get("page"))?1:Integer.parseInt((String) map.get("page"));
        Integer limit = StringUtils.isEmpty(map.get("limit"))?5:Integer.parseInt((String)map.get("limit"));
        ScheduleQueryVo scheduleQueryVo = new ScheduleQueryVo();
        scheduleQueryVo.setHospCode((String) map.get("hospCode"));
        return Result.builds(scheduleService.findPageSchedule(page,limit,scheduleQueryVo),ResultCodeEnum.SUCCESS);
    }

    //删除排班
    @PostMapping("/removeSchedule")
    public Result removeSchedule(@RequestParam Map<String,Object> map){
        String hospCode = (String) map.get("hospCode");
        String hosScheduleId = (String) map.get("hosScheduleId");
        scheduleService.remove(hospCode,hosScheduleId);
        return Result.builds(ResultCodeEnum.SUCCESS);
    }
}
