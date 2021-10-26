package com.hjh.syt.controller.api;
import com.hjh.syt.HospitalException;
import com.hjh.syt.Result;
import com.hjh.syt.ResultCodeEnum;
import com.hjh.syt.service.HospitalService;
import com.hjh.syt.service.HospitalSetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

/**
 * @author: hasee
 * @date: 2021/6/26 10:32
 * @description:
 */

@RestController
@RequestMapping("/api/hosp")
public class ApiHospitalController {
    @Autowired
    private HospitalService hospitalService;

    //上传医院接口
    @PostMapping("/saveHospital")
    public Result saveHosp(@RequestParam Map<String,Object> map) {
        //获取医院系统传递过来的签名
//        String hospSign = (String) map.get("sign");
        //根据传递过来的医院编码查数据库查询签名
//        String hospCode = (String) map.get("hospCode");

//        if (!hospitalSetService.checkSign(hospCode,hospSign)){
//            throw new HospitalException(ResultCodeEnum.SIGN_ERROR);
//        }else {
        String picture = (String) map.get("picture");
        picture = picture.replaceAll(" ","+");
        map.put("picture",picture);
        hospitalService.save(map);
            return Result.builds(ResultCodeEnum.SUCCESS);
//        }
    }

    //查询医院端口
    @PostMapping("/hospital/show")
    public Result getHospital(@RequestParam Map<String,Object> map){
        //获取医院编号
        String hospCode = (String) map.get("hospCode");
        //获取签名
//        String sign = (String)map.get("sign");
        //判断签名是否正确
//        if (!hospitalSetService.checkSign(hospCode,sign)){
//            throw new HospitalException(ResultCodeEnum.SIGN_ERROR);
//        }else {
            return Result.builds(hospitalService.getByHospital(hospCode),ResultCodeEnum.SUCCESS);
//        }
    }
}
