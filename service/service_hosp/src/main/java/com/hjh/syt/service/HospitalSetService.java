package com.hjh.syt.service;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hjh.syt.entity.hosp.HospitalSet;
import com.hjh.syt.vo.order.SignInfoVo;

/**
 * @author: hasee
 * @date: 2021/6/14 23:20
 * @description:
 */
public interface HospitalSetService extends IService<HospitalSet>{

    //获取医院签名
    String getSignKey(String hospCode);
    //校验签名是否一致
    Boolean checkSign(String hospCode,String hospSign);
    //获取医院签名信息
    SignInfoVo getSignInfoVo(String hospCode);
}
