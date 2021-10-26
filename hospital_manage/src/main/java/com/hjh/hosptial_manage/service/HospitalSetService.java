package com.hjh.hosptial_manage.service;

import com.hjh.syt.vo.eml.EmailVo;

public interface HospitalSetService {
    boolean selectORAdd(EmailVo emailVo) throws Exception;
}
