package com.hjh.syt.service;

import com.hjh.syt.entity.hosp.Schedule;
import com.hjh.syt.vo.hosp.ScheduleOrderVo;
import com.hjh.syt.vo.hosp.ScheduleQueryVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

/**
 * @author: hasee
 * @date: 2021/7/2 21:01
 * @description:
 */

public interface ScheduleService {
    //上传接口
    void saveSchedule(Map<String, Object> map);

    Page<Schedule> findPageSchedule(Integer page, Integer limit, ScheduleQueryVo scheduleQueryVo);

    void remove(String hospCode, String hosScheduleId);
    //根据医院编号 和 科室编号 ，查询排班规则数据
    Map<String, Object> getRuleSchedule(long page, long limit, String hoscode, String depcode);

    //根据医院编号 、科室编号和工作日期，查询排班详细信息
    List<Schedule> getDetailSchedule(String hoscode, String depcode, String workDate);

    //获取可预约的排班数据
    Map<String,Object> getBookingScheduleRule(int page,int limit,String hoscode,String depcode);

    //获取排班id获取排班数据
    Schedule getScheduleId(String scheduleId);

    //根据排班id获取预约下单数据
    ScheduleOrderVo getScheduleOrderVo(String scheduleId);

    //更新排班数据 用于mp
    void update(Schedule schedule);
}
