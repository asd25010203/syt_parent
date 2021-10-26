package com.hjh.hosptial_manage.receiver;
import com.hjh.hosptial_manage.service.HospitalSetService;
import com.hjh.syt.rabbit.constant.MqConst;
import com.hjh.syt.rabbit.service.RabbitmqService;
import com.hjh.syt.vo.eml.EmailVo;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.io.IOException;

@Component
@Slf4j
public class HospitalReceiver {

    @Resource
    private HospitalSetService hospitalSetService;

    @RabbitListener(bindings = {@QueueBinding(
            value = @Queue(value = MqConst.QUEUE_ADD,arguments = {
                    @Argument(name = "x-dead-letter-exchange",value = MqConst.ExCHANGE_DLX), //死信交换机
                    @Argument(name = "x-dead-letter-routing-key",value = MqConst.ROUTING_DLX), //死信路由键
                    @Argument(name = "x-message-ttl",value = "10000",type = "java.lang.Integer") //消息消费超时
            },durable="true",autoDelete="false"),
            exchange = @Exchange(value = MqConst.EXCHANGE_DIRECT_ADD),
            key = {MqConst.ROUTING_ADD}
    )})
    public void addHosp(EmailVo emailVo, Message message, Channel channel){
        try {
            hospitalSetService.selectORAdd(emailVo);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
            log.info("消费成功："+emailVo);
        } catch (Exception e) {
            try {
                log.error("消费失败："+emailVo);
                channel.basicNack(message.getMessageProperties().getDeliveryTag(),false,false);
                log.warn("消息进入死信");
                e.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
