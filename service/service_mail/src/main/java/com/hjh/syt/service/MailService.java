package com.hjh.syt.service;

import com.hjh.syt.vo.eml.EmailVo;
import com.hjh.syt.vo.eml.MailVo;

public interface MailService {
    public boolean LoginMailCheck(String mailNumber,String code);
    public void HospRegisterMail(EmailVo emailVo) throws Exception;
    public void Email(MailVo mailVo) throws Exception;
}
