package com.hjh.syt.service.Impl;

import com.alibaba.fastjson.JSONObject;
import com.hjh.syt.entity.hosp.Department;
import com.hjh.syt.repository.DepartmentRepository;
import com.hjh.syt.service.DepartmentService;
import com.hjh.syt.vo.hosp.DepartmentQueryVo;
import com.hjh.syt.vo.hosp.DepartmentVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author: hasee
 * @date: 2021/6/29 16:50
 * @description:
 */

@Service
public class DepartmentServiceImpl implements DepartmentService {

    @Autowired
    private DepartmentRepository  departmentRepository;


    @Override
    public void save(Map map) {
        //将map对象转换为json字符串
        String jsonString = JSONObject.toJSONString(map);
        //将json对象转换为对应的对象
        Department department = JSONObject.parseObject(jsonString, Department.class);
        //根据医院编号和科室编号查询数据
        Department departmentExist = departmentRepository.
                findDepartmentByHospCodeAndDepCode(department.getHospCode(),department.getDepCode());
        if (departmentExist != null){
            //修改操作
            departmentExist.setCreateTime(departmentExist.getCreateTime());
            departmentExist.setUpdateTime(new Date());
            departmentExist.setIsDeleted(0);
            departmentRepository.save(departmentExist);
        }else {
            //添加操作
            department.setCreateTime(new Date());
            department.setUpdateTime(new Date());
            department.setIsDeleted(0);
            departmentRepository.save(department);
        }
    }

    @Override
    public Page<Department> findPageDepartment(Integer page, Integer limit, DepartmentQueryVo departmentQueryVo) {
        //创建一个pageable对象，设置当前页和每页记录数
        Pageable pageable = PageRequest.of(page-1,limit);
        //创建Example对象
        Department department = new Department();
        BeanUtils.copyProperties(departmentQueryVo,department);
        //设置逻辑删除默认值
        department.setIsDeleted(0);
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING) //模糊查询匹配条件
                .withIgnoreCase(true); //忽略大小写
        Example<Department> example = Example.of(department,matcher);
        return departmentRepository.findAll(example,pageable);
    }

    @Override
    public void remove(String hospCode, String depCode) {
        //根据医院编号和科室编号查询
        Department department = departmentRepository.findDepartmentByHospCodeAndDepCode(hospCode, depCode);
        if (department != null){
            departmentRepository.deleteById(department.getId());
        }
    }
    //根据医院编号，查询医院所有科室列表
    @Override
    public List<DepartmentVo> findDeptTree(String hoscode) {
        //创建list集合，用于最终数据封装
        List<DepartmentVo> result = new ArrayList<>();

        //根据医院编号，查询医院所有科室信息
        Department departmentQuery = new Department();
        departmentQuery.setHospCode(hoscode);
        Example example = Example.of(departmentQuery);
        //所有科室列表 departmentList
        List<Department> departmentList = departmentRepository.findAll(example);

        //根据大科室编号  bigcode 分组，获取每个大科室里面下级子科室
        Map<String, List<Department>> deparmentMap =
                departmentList.stream().collect(Collectors.groupingBy(Department::getBigCode));
        //遍历map集合 deparmentMap
        for(Map.Entry<String,List<Department>> entry : deparmentMap.entrySet()) {
            //大科室编号
            String bigcode = entry.getKey();
            //大科室编号对应的全局数据
            List<Department> deparment1List = entry.getValue();
            //封装大科室
            DepartmentVo departmentVo1 = new DepartmentVo();
            departmentVo1.setDepcode(bigcode);
            departmentVo1.setDepname(deparment1List.get(0).getBigName());

            //封装小科室
            List<DepartmentVo> children = new ArrayList<>();
            for(Department department: deparment1List) {
                DepartmentVo departmentVo2 =  new DepartmentVo();
                departmentVo2.setDepcode(department.getDepCode());
                departmentVo2.setDepname(department.getDepName());
                //封装到list集合
                children.add(departmentVo2);
            }
            //把小科室list集合放到大科室children里面
            departmentVo1.setChildren(children);
            //放到最终result里面
            result.add(departmentVo1);
        }
        //返回
        return result;
    }

    //根据科室编号，和医院编号，查询科室名称
    @Override
    public String getDepName(String hoscode, String depcode) {
        Department department = departmentRepository.findDepartmentByHospCodeAndDepCode(hoscode, depcode);
        if(department != null) {
            return department.getDepName();
        }
        return null;
    }

    @Override
    public Department getDepartment(String hoscode, String depcode) {
        return departmentRepository.findDepartmentByHospCodeAndDepCode(hoscode, depcode);
    }
}
