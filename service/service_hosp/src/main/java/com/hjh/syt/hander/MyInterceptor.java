package com.hjh.syt.hander;
import com.hjh.syt.HospitalException;
import com.hjh.syt.ResultCodeEnum;
import com.hjh.syt.service.HospitalSetService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
/**
 * @author: hasee
 * @date: 2021/7/3 15:12
 * @description:
 */
@Slf4j
public class MyInterceptor implements HandlerInterceptor {
    @Resource
    private HospitalSetService hospitalSetService;

    //在业务处理器处理请求之前被调用。预处理，可以进行编码、安全控制、权限校验等处理
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String,Object> resultMap = new HashMap<>();
        for (Map.Entry<String,String[]> param:parameterMap.entrySet()){
            resultMap.put(param.getKey(),param.getValue()[0]);
        }
        log.info("开始校验签名......");
        if (hospitalSetService.
                checkSign((String) resultMap.get("hospCode"),(String) resultMap.get("sign"))){
            return true;
        }else {
            throw new HospitalException(ResultCodeEnum.SIGN_ERROR);
        }
    }

    //在业务处理器处理请求执行完成后，生成视图之前执行。后处理（调用了Service并返回ModelAndView，但未进行页面渲染），有机会修改ModelAndView；
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    //在DispatcherServlet完全处理完请求后被调用，可用于清理资源等。返回处理（已经渲染了页面）
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
