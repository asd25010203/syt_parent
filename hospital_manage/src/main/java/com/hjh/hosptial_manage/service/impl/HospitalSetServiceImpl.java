package com.hjh.hosptial_manage.service.impl;

import cn.hutool.log.Log;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hjh.hosptial_manage.mapper.HospitalSetMapper;
import com.hjh.hosptial_manage.model.HospitalSet;
import com.hjh.hosptial_manage.service.HospitalSetService;
import com.hjh.syt.rabbit.constant.MqConst;
import com.hjh.syt.rabbit.service.RabbitmqService;
import com.hjh.syt.vo.eml.EmailVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
@Slf4j
public class HospitalSetServiceImpl implements HospitalSetService {
    @Resource
    private HospitalSetMapper hospitalSetMapper;
    @Resource
    private RabbitmqService rabbitmqService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean selectORAdd(EmailVo emailVo) throws Exception{
        HospitalSet hospitalSet = new HospitalSet();
        hospitalSet.setHoscode(String.valueOf(emailVo.getParam().get("hospCode")));
        hospitalSet.setApiUrl(String.valueOf(emailVo.getParam().get("aipUrl")));
        hospitalSet.setSignKey(String.valueOf(emailVo.getParam().get("SignKey")));
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("hoscode",hospitalSet.getHoscode());
        if (hospitalSetMapper.selectOne(queryWrapper)==null){
            hospitalSetMapper.insert(hospitalSet);
            rabbitmqService.sendMessage(MqConst.EXCHANGE_DIRECT_EMAIL,MqConst.ROUTING_EMAIL,emailVo);
        }else {
            hospitalSetMapper.updateById(hospitalSet);
        }
        return true;
    }
}
