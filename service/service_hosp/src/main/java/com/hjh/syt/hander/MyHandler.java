package com.hjh.syt.hander;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
/**
 * @author: hasee
 * @date: 2021/6/16 10:40
 * @description:
 */
@Component
public class MyHandler implements MetaObjectHandler {



    @Override
    public void insertFill(MetaObject metaObject) {
        this.setFieldValByName("version",1,metaObject);//每次添加自动生成版本
    }

    @Override
    public void updateFill(MetaObject metaObject) {

    }
}
