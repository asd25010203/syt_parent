package com.hjh.syt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hjh.syt.entity.user.Patient;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PatientMapper extends BaseMapper<Patient> {
}
