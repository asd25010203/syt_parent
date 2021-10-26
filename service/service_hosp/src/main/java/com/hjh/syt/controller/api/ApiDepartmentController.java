package com.hjh.syt.controller.api;
import cn.hutool.crypto.SecureUtil;
import com.hjh.syt.HospitalException;
import com.hjh.syt.Result;
import com.hjh.syt.ResultCodeEnum;
import com.hjh.syt.service.DepartmentService;
import com.hjh.syt.service.HospitalSetService;
import com.hjh.syt.vo.hosp.DepartmentQueryVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;


/**
 * @author: hasee
 * @date: 2021/6/29 16:52
 * @description:
 */
@RestController
@RequestMapping("/api/dep")
@Slf4j
public class ApiDepartmentController {
//    @Autowired
//    private HospitalSetService hospitalSetService;
    @Autowired
    private DepartmentService departmentService;

    @PostMapping("/department/remove")
    public Result removeDepartment(@RequestParam Map map){
        //获取医院编号
        String hospCode = (String) map.get("hospCode");
//        //获取医院签名
//        String hospSign = (String) map.get("sign");
        //获取科室编号
        String depCode = (String) map.get("depCode");
//        if (!hospitalSetService.checkSign(hospCode,hospSign)){
//            throw new HospitalException(ResultCodeEnum.SIGN_ERROR);
//        }else {
            departmentService.remove(hospCode,depCode);
            return Result.builds(ResultCodeEnum.SUCCESS);
//        }
    }
    //上传科室
    @PostMapping("/saveDepartment")
    public Result saveDepartment(@RequestParam Map<String,Object> map){
        //获取医院编号
//        String hospCode = (String) map.get("hospCode");
//        //获取医院签名
//        String hospSign = (String) map.get("sign");
//        if (!hospitalSetService.checkSign(hospCode,hospSign)){
//            throw new HospitalException(ResultCodeEnum.SIGN_ERROR);
//        }else {
            departmentService.save(map);
            return Result.builds(ResultCodeEnum.SUCCESS);
//        }
    }
    //分页查询科室
    @PostMapping("/department/list")
    public Result PageDepartment(@RequestParam Map<String,Object> map){

        //获取医院编号
        String hospCode = (String)map.get("hospCode");
        //获取当前页
        Integer page = StringUtils.isEmpty(map.get("page"))? 1 : Integer.parseInt((String) map.get("page"));
        //获取limit
        Integer limit = StringUtils.isEmpty(map.get("limit"))? 5 : Integer.parseInt((String) map.get("limit"));
        //签名校验
//        String hospSign = (String) map.get("sign");
//        if (!hospitalSetService.checkSign(hospCode,hospSign)){
//            throw new HospitalException(ResultCodeEnum.SIGN_ERROR);
//        }else {
            DepartmentQueryVo departmentQueryVo = new DepartmentQueryVo();
            departmentQueryVo.setHospCode(hospCode);
            return Result.builds(departmentService.findPageDepartment(page,limit,departmentQueryVo),ResultCodeEnum.SUCCESS);
//        }
    }
}
