package com.hjh.syt.api;
import com.hjh.syt.service.PatientService;
import com.hjh.syt.AuthContextHolder;
import com.hjh.syt.Result;
import com.hjh.syt.ResultCodeEnum;
import com.hjh.syt.entity.user.Patient;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

//就诊人管理接口
@RestController
@RequestMapping("/api/user/patient")
public class PatientApiController {

    @Resource
    private PatientService patientService;

    //获取就诊人列表
    @GetMapping("auth/findAll")
    public Result findAll(HttpServletRequest request) {
        //获取当前登录用户id
        Long userId = AuthContextHolder.getUserId(request);
        System.out.println(userId);
        List<Patient> list = patientService.findAllUserId(userId);
        return Result.builds(list,ResultCodeEnum.SUCCESS);
    }

    //添加就诊人
    @PostMapping("auth/save")
    public Result savePatient(@RequestBody Patient patient, HttpServletRequest request) {
        //获取当前登录用户id
        Long userId = AuthContextHolder.getUserId(request);
        patient.setUserId(userId);
        patientService.save(patient);
        return Result.builds(ResultCodeEnum.SUCCESS);
    }

    //根据id获取就诊人信息
    @GetMapping("auth/get/{id}")
    public Result getPatient(@PathVariable Long id) {
        Patient patient = patientService.getPatientId(id);
        return Result.builds(patient,ResultCodeEnum.SUCCESS);
    }

    //修改就诊人
    @PostMapping("auth/update")
    public Result updatePatient(@RequestBody Patient patient) {
        patientService.updateById(patient);
        return Result.builds(ResultCodeEnum.SUCCESS);
    }

    //删除就诊人
    @DeleteMapping("auth/remove/{id}")
    public Result removePatient(@PathVariable Long id) {
        patientService.removeById(id);
        return Result.builds(ResultCodeEnum.SUCCESS);
    }

    //根据就诊人id获取就诊人信息
    @GetMapping("inner/get/{id}")
    public Patient getPatientOrder(@PathVariable Long id) {
        Patient patient = patientService.getPatientId(id);
        return patient;
    }
}
