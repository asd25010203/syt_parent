package com.hjh.syt.repository;

import com.hjh.syt.entity.hosp.Department;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
/**
 * @author: hasee
 * @date: 2021/6/29 16:45
 * @description:
 */
@Repository
public interface DepartmentRepository extends MongoRepository<Department,String> {

    Department findDepartmentByHospCodeAndDepCode(String hospCode, String depCode);
}
