package com.hjh.syt.service;

import com.hjh.syt.entity.hosp.Hospital;
import com.hjh.syt.vo.hosp.HospitalQueryVo;
import com.hjh.syt.vo.hosp.HospitalSetQueryVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;
/**
 * @author: hasee
 * @date: 2021/6/26 10:29
 * @description:
 */
public interface HospitalService {
    //上传医院信息
    void save(Map<String, Object> map);
    //根据编号获取医院信息
    Hospital getByHospital(String hospCode);
    //医院列表(条件查询分页)
    Page<Hospital> selectHospPage(Integer page, Integer limit, HospitalQueryVo hospitalQueryVo);

    //更新医院上线状态
    void updateStatus(String hospCode, Integer status);

    //医院详情信息
    Map<String, Object> getHospById(String id);

    //获取医院名称
    String getHospName(String hoscode);

    //根据医院名称查询
    List<Hospital> findByHosname(String hosname);

    //根据医院编号获取医院预约挂号详情
    Map<String, Object> item(String hoscode);
}
