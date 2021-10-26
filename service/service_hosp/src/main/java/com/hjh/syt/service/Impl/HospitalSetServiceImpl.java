package com.hjh.syt.service.Impl;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hjh.syt.dao.HospitalSetDao;
import com.hjh.syt.entity.hosp.HospitalSet;
import com.hjh.syt.service.HospitalSetService;
import com.hjh.syt.vo.order.SignInfoVo;
import org.springframework.stereotype.Service;


/**
 * @author: hasee
 * @date: 2021/6/14 23:25
 * @description:
 */

@Service
public class HospitalSetServiceImpl extends ServiceImpl<HospitalSetDao, HospitalSet> implements HospitalSetService {

    @Override
    public String getSignKey(String hospCode) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("hosp_code",hospCode);
        return baseMapper.selectOne(queryWrapper).getSignKey();
    }

    @Override
    public Boolean checkSign(String hospCode, String hospSign) {
        String hospSignKey = this.getSignKey(hospCode);
        //把数据库查询签名进行加密
        String Key = SecureUtil.md5(hospSignKey);
        //判断内容是否一致
        return Key.equals(hospSign);
    }

    @Override
    public SignInfoVo getSignInfoVo(String hospCode) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("hosp_code",hospCode);
        HospitalSet hospitalSet = baseMapper.selectOne(queryWrapper);
        SignInfoVo signInfoVo = new SignInfoVo();
        signInfoVo.setSignKey(hospitalSet.getSignKey());
        signInfoVo.setApiUrl(hospitalSet.getApiUrl());
        return signInfoVo;
    }
}
