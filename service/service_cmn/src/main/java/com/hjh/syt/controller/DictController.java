package com.hjh.syt.controller;
import com.hjh.syt.Result;
import com.hjh.syt.ResultCodeEnum;
import com.hjh.syt.entity.cmn.Dict;
import com.hjh.syt.service.DictService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;


/**
 * @author: hasee
 * @date: 2021/6/20 22:19
 * @description:
 */
@Api("数据字典接口")
@RestController
@RequestMapping("/admin/cmn/dict")
//@CrossOrigin //允许跨界访问
public class DictController {

    @Resource
    private DictService dictService;

    @ApiOperation("根据数据id查询子数据列表")
    @GetMapping("/findByIdData/{id}")
    public Result findByIdData(@PathVariable("id") Long parentId){
        return Result.builds(dictService.findByIdData(parentId), ResultCodeEnum.SUCCESS);
    }
    @ApiOperation(value = "根据dictCode获取下级节点")
    @GetMapping("findByDictCode/{dictCode}")
    public Result findByDictCode(@PathVariable String dictCode) {
        List<Dict> list = dictService.findByDictCode(dictCode);
        return Result.builds(list,ResultCodeEnum.SUCCESS);
    }

    @ApiOperation("导出数据字典")
    @GetMapping("/ReadDict")
    public void ReadDict(HttpServletResponse response){
        dictService.ReadDict(response);
    }

    @ApiOperation("导入数据字典")
    @PostMapping("/WriterDict")
    public Result WriterDict(@RequestParam MultipartFile file){
        dictService.WriterDict(file);
        return Result.builds(ResultCodeEnum.SUCCESS);
    }
    //根据dictcode和value查询
    @GetMapping("getName/{dictCode}/{value}")
    public String getName(@PathVariable String dictCode,
                          @PathVariable String value) {
        String dictName = dictService.getDictName(dictCode,value);
        return dictName;
    }

    //根据value查询
    @GetMapping("getName/{value}")
    public String getName(@PathVariable String value) {
        String dictName = dictService.getDictName("",value);
        return dictName;
    }
}
