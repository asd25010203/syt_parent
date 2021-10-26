package com.hjh.syt.rabbit.constant;

/**
    mq链接常量

  **/
public class MqConst {
    //交换机
    public static final String EXCHANGE_DIRECT_EMAIL = "exchange.direct.email";
    //队列
    public static final String QUEUE_EMAIL = "queue.email";
    //路由
    public static final String ROUTING_EMAIL = "email";

    public static final String EXCHANGE_DIRECT_ADD = "exchange.direct.add";
    public static final String QUEUE_DLX="queue.dlx";
    public static final String ROUTING_DLX = "add.dlx";

    public static final String ExCHANGE_DLX = "exchange.dlx";
    public static final String QUEUE_ADD="queue.add";
    public static final String ROUTING_ADD = "add";

    public static final String EXCHANGE_DIRECT_ORDER="exchange.direct.order";
    public static final String ROUTING_ORDER="order";
    public static final String QUEUE_ORDER="queue.order";

    public static final String QUEUE_TASK_8 = "queue.task.8";
    public static final String EXCHANGE_DIRECT_TASK = "exchange.direct.task";
    public static final String ROUTING_TASK_8 = "task.8";
}
