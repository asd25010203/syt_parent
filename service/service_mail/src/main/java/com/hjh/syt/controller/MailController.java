package com.hjh.syt.controller;

import cn.hutool.core.util.RandomUtil;
import com.hjh.syt.Result;
import com.hjh.syt.ResultCodeEnum;
import com.hjh.syt.service.MailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/admin/mail/")
@Slf4j
public class MailController {
    @Resource
    private MailService mailService;
    @Resource
    private RedisTemplate<String,String> redisTemplate;

    @GetMapping("loginMail/{mobile}")
    public Result loginMail(@PathVariable String mobile){

        if (!StringUtils.isEmpty(redisTemplate.boundValueOps(mobile).get())){
            return Result.builds(ResultCodeEnum.SUCCESS);
        }
        String randomNumbers = RandomUtil.randomNumbers(4);
        boolean loginMailCheck = mailService.LoginMailCheck(mobile, randomNumbers);
        if (loginMailCheck){
            redisTemplate.boundValueOps(mobile).set(randomNumbers,60, TimeUnit.SECONDS);
            log.info("发送注册邮件成功");
            return Result.builds(ResultCodeEnum.SUCCESS);
        }
        log.info("发送注册邮件失败");
        return Result.builds(ResultCodeEnum.FALL);
    }
}
