package com.hjh.syt.receiver;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hjh.syt.rabbit.constant.MqConst;
import com.hjh.syt.rabbit.service.RabbitmqService;
import com.hjh.syt.service.HospitalSetService;
import com.hjh.syt.vo.eml.EmailVo;
import com.hjh.syt.vo.eml.MailVo;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
@Slf4j
public class HospitalSetReceiver {
    @Resource
    RedisTemplate<Object,Object> redisTemplate;
    @Resource
    RabbitmqService rabbitmqService;

    @RabbitListener(bindings = {@QueueBinding(
            value = @Queue(value = MqConst.QUEUE_DLX),
            exchange = @Exchange(value = MqConst.ExCHANGE_DLX),
            key = {MqConst.ROUTING_DLX}
    )})
    public void HospitalSetUp(EmailVo emailVo, Message message, Channel channel) throws Exception{
        if (redisTemplate.opsForHash().get("DeadLetter",emailVo.getParam().get("code"))==null){
            log.warn("未添加医院配置....");
            MailVo mailVo = new MailVo();
            mailVo.setContent("未添加医院配置出错，重试或反映人工客服");
            mailVo.setEmailVo(emailVo);
            rabbitmqService.sendMessage(MqConst.EXCHANGE_DIRECT_EMAIL,MqConst.ROUTING_EMAIL,mailVo);
            redisTemplate.opsForHash().put("DeadLetter",emailVo.getParam().get("code"),emailVo);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        }else {
            channel.basicNack(message.getMessageProperties().getDeliveryTag(),false,false);
        }
    }
}
