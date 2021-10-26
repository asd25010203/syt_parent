package com.hjh.syt.repository;

import cn.hutool.core.date.DateTime;
import com.hjh.syt.entity.hosp.Schedule;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * @author: hasee
 * @date: 2021/7/2 20:58
 * @description:
 */

@Repository
public interface ScheduleRepository extends MongoRepository<Schedule,String> {

    Schedule findScheduleByHospCodeAndHosScheduleId(String hospCode, String HosScheduleId);
    List<Schedule> findScheduleByHospCodeAndDepCodeAndWorkDate(String hospCode, String depCode, Date workDate);
}