package com.hjh.syt.receiver;
import com.hjh.syt.rabbit.constant.MqConst;
import com.hjh.syt.service.MailService;
import com.hjh.syt.vo.eml.EmailVo;
import com.hjh.syt.vo.eml.MailVo;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;

@Component
@Slf4j
@RabbitListener(bindings = {@QueueBinding(
        value = @Queue(value = MqConst.QUEUE_EMAIL,durable="true",autoDelete="false"),
        exchange = @Exchange(value = MqConst.EXCHANGE_DIRECT_EMAIL),
        key = {MqConst.ROUTING_EMAIL}
)})
public class MailReceiver {
    @Resource
    private MailService mailService;
    @Resource
    private RedisTemplate<Object,Object> redisTemplate;

    @RabbitHandler
    public void HospRegisterMail(EmailVo emailVo, Message message, Channel channel){
        try {
            mailService.HospRegisterMail(emailVo);
            //消息确认
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
            log.info("成功消费:"+emailVo);
        } catch (Exception e) {
            log.error("消费失败"+emailVo);
            e.printStackTrace();
            try {
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
                redisTemplate.opsForHash().put("mailError",emailVo.getParam().get("code"),emailVo);
                log.error("邮件服务故障");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    @RabbitHandler
    public void HospRegisterMail(MailVo mailVo, Message message, Channel channel){
        try {
            mailService.Email(mailVo);
            //消息确认
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
            log.info("成功消费:"+mailVo);
        } catch (Exception e) {
            log.error("消费失败"+mailVo);
            e.printStackTrace();
            try {
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
                redisTemplate.opsForHash().put("mailError",mailVo.getEmailVo().getParam().get("code"),mailVo);
                log.error("邮件服务故障");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
