package com.hjh.syt.service;
import com.hjh.syt.entity.hosp.Department;
import com.hjh.syt.vo.hosp.DepartmentQueryVo;
import com.hjh.syt.vo.hosp.DepartmentVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;
/**
 * @author: hasee
 * @date: 2021/6/29 16:46
 * @description:
 */
public interface DepartmentService {
    //上传科室接口
    void save(Map<String, Object> map);

    //分页查询
    Page<Department> findPageDepartment(Integer page, Integer limit, DepartmentQueryVo departmentQueryVo);

    //删除
    void remove(String hospCode, String depCode);
    //根据医院编号，查询医院所有科室列表
    List<DepartmentVo> findDeptTree(String hoscode);

    //根据科室编号，和医院编号，查询科室名称
    String getDepName(String hoscode, String depcode);

    //根据科室编号，和医院编号，查询科室
    Department getDepartment(String hoscode, String depcode);
}
