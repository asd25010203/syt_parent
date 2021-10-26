package com.hjh.syt.service.impl;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.extra.mail.MailAccount;
import cn.hutool.extra.mail.MailUtil;
import com.hjh.syt.conf.MailParameter;
import com.hjh.syt.service.MailService;
import com.hjh.syt.vo.eml.EmailVo;
import com.hjh.syt.vo.eml.MailVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class MailServiceImpl implements MailService {

    @Resource
    private MailParameter mailParameter;

    @Override
    public boolean LoginMailCheck(String mailNumber,String code){
        try {
            if (mailNumber==null) return false;
            MailUtil.send(this.SetUpMailAccount(),mailNumber,"验证码",code,false);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    @Override
    public void HospRegisterMail(EmailVo emailVo) throws Exception{
            log.info("邮件配置"+this.SetUpMailAccount());
            MailUtil.send(this.SetUpMailAccount(),emailVo.getMailNumber(),
                    "syt","恭喜入驻成功登录账号："+emailVo.getParam().get("hospCode")+
                            ",请到"+emailVo.getParam().get("toUrl")+"进行设置",false);
    }

    @Override
    public void Email(MailVo mailVo) throws Exception{
        log.info("邮件配置"+this.SetUpMailAccount());
        MailUtil.send(this.SetUpMailAccount(), mailVo.getEmailVo().getMailNumber(),"syt", mailVo.getContent(),false);
    }
    private MailAccount SetUpMailAccount(){
        MailAccount account = new MailAccount();
        account.setHost(mailParameter.getHost());
        account.setPort(mailParameter.getPort());
        account.setFrom(mailParameter.getFrom());
        account.setUser(mailParameter.getUser());
        account.setPass(mailParameter.getPass());
        account.setStarttlsEnable(mailParameter.getStarttlsEnable());
        account.setSslEnable(mailParameter.getSslEnable());
        account.setSocketFactoryClass(mailParameter.getSocketFactoryClass());
        account.setSocketFactoryFallback(mailParameter.getSocketFactoryFallback());
        account.setSocketFactoryPort(mailParameter.getSocketFactoryPort());
        account.setTimeout(mailParameter.getTimeout());
        account.setConnectionTimeout(mailParameter.getConnectionTimeout());
        return account;
    }
}
