package com.hjh.syt.controller;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hjh.syt.Result;
import com.hjh.syt.ResultCodeEnum;
import com.hjh.syt.entity.hosp.Hospital;
import com.hjh.syt.entity.hosp.HospitalSet;
import com.hjh.syt.service.HospitalService;
import com.hjh.syt.service.HospitalSetService;
import com.hjh.syt.vo.hosp.HospitalQueryVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("/admin/hosp/hospital")
//@CrossOrigin
public class HospitalController {

    @Resource
    private HospitalService hospitalService;
    @Resource
    private HospitalSetService hospitalSetService;

    //医院列表(条件查询分页)
    @GetMapping("list/{page}/{limit}")
    public Result listHosp(@PathVariable Integer page,
                           @PathVariable Integer limit,
                           HospitalQueryVo hospitalQueryVo) {
        Page<Hospital> pageModel = hospitalService.selectHospPage(page,limit, hospitalQueryVo);
        return Result.builds(pageModel, ResultCodeEnum.SUCCESS);
    }

    //更新医院上线状态
    @ApiOperation(value = "更新医院上线状态")
    @GetMapping("updateHospStatus/{hospCode}/{status}")
    public Result updateHospStatus(@PathVariable String hospCode,@PathVariable Integer status) {
        hospitalService.updateStatus(hospCode,status);
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("hosp_code",hospCode);
        HospitalSet hospitalSet = hospitalSetService.getOne(queryWrapper);
        hospitalSet.setStatus(status);
        hospitalSetService.updateById(hospitalSet);
        return Result.builds(ResultCodeEnum.SUCCESS);
    }

    //医院详情信息
    @ApiOperation(value = "医院详情信息")
    @GetMapping("showHospDetail/{id}")
    public Result showHospDetail(@PathVariable String id) {
        Map<String, Object> map = hospitalService.getHospById(id);
        return Result.builds(map,ResultCodeEnum.SUCCESS);
    }
}
