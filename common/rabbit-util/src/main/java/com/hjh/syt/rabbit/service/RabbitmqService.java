package com.hjh.syt.rabbit.service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;

@Service
@Slf4j
public class RabbitmqService {

    @Resource
    private RabbitTemplate rabbitTemplate;

    public void sendMessage(String exchange, String routingKey, Object message){
        try {
            //重发策略
            RetryTemplate retryTemplate = new RetryTemplate();
            retryTemplate.setRetryPolicy(new SimpleRetryPolicy(3));
            rabbitTemplate.setRetryTemplate(retryTemplate);
            //确认消息是否发到交换机
            rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
                if (!ack){
                    log.error("correlationData："+ correlationData);
                    log.error("ack："+ false);
                    log.error("cause："+cause);
                }
            });
            //确认消息是否发到队列
            rabbitTemplate.setReturnsCallback(returnedMessage -> {
                log.error("返回消息配置:"+returnedMessage.getMessage().getMessageProperties().toString());
                log.error("反馈代码:"+returnedMessage.getReplyCode());
                log.error("反馈内容:"+returnedMessage.getReplyText());
                log.error("Exchange:"+returnedMessage.getExchange());
                log.error("RoutingKey:"+returnedMessage.getRoutingKey());
            });
            rabbitTemplate.convertAndSend(exchange,routingKey,message);
            log.info("发送端发送："+message);
        } catch (AmqpException e) {
            log.error("发送失败：原因重连3次未连上");
            e.printStackTrace();
        }
    }
}
