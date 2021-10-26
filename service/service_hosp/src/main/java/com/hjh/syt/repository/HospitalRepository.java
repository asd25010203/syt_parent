package com.hjh.syt.repository;

import com.hjh.syt.entity.hosp.Hospital;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author: hasee
 * @date: 2021/6/26 10:22
 * @description:
 */

@Repository
public interface HospitalRepository extends MongoRepository<Hospital,String> {
    //判断是否存在数据
    Hospital findHospitalByHospCode(String hosCode);
    //根据医院名称查询
    List<Hospital> findHospitalByHospNameLike(String hospName);
}
