package com.hjh.syt.service.Impl;
import cn.hutool.core.io.IoUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hjh.syt.dao.DictDao;
import com.hjh.syt.entity.cmn.Dict;
import com.hjh.syt.service.DictService;
import com.hjh.syt.vo.cmn.DictEnVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: hasee
 * @date: 2021/6/20 22:24
 * @description:
 */
@Service
@Slf4j
public class DictServiceImpl extends ServiceImpl<DictDao, Dict> implements DictService {

    @Cacheable(value = "dict",keyGenerator = "keyGenerator")
    //根据id查询子数据列表
    public List<Dict> findByIdData(Long parentId){
        log.info("开始查询");
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("parent_id",parentId);
        List<Dict> selectList = baseMapper.selectList(queryWrapper);
        //向list集合设置hasChildren,hasChildren：记录是否有节点
        for (Dict dict:selectList){
            Long dictId = dict.getId();
            boolean isChild = this.isChildren(dictId);
            dict.setHasChildren(isChild);
        }
        return selectList;
    }

    @Override
    public void ReadDict(HttpServletResponse response) {
        //查询数据
        List<Dict> dictList = baseMapper.selectList(null);
        List<DictEnVo> dictEnVoList = new ArrayList<>();
        //将dict数据转移到dictEnVo
        for (Dict dict:dictList){
            DictEnVo dictEnVo = new DictEnVo();
            BeanUtils.copyProperties(dict,dictEnVo);
            dictEnVoList.add(dictEnVo);
        }
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String filename = "dict";
        response.setHeader("Content-Disposition","attachment;filename="+filename+".xlsx");

        ExcelWriter writer = ExcelUtil.getWriter(true);
        writer.merge(4, "数据字典");
        writer.write(dictEnVoList, true);

        try {
            writer.flush(response.getOutputStream(), true);
            IoUtil.close(response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            writer.close();
        }

    }

    @Override
    public void WriterDict(MultipartFile file) {
        ExcelReader reader = null;
        try {
            reader = ExcelUtil.getReader(file.getInputStream());
            List<DictEnVo> dictEnVoList = reader.read(1, 1, DictEnVo.class);
            for(DictEnVo dictEnVo:dictEnVoList){
                Dict dict = new Dict();
                BeanUtils.copyProperties(dictEnVo,dict);
                baseMapper.insert(dict);
                IoUtil.close(file.getInputStream());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            reader.close();
        }
    }

    //根据id判断下面是否有子节点
    private boolean isChildren(Long parentId){
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("parent_id",parentId);
        Integer selectCount = baseMapper.selectCount(queryWrapper);
        return selectCount>0;
    }
    //根据dictcode和value查询
    @Override
    public String getDictName(String dictCode, String value) {
        //如果dictCode为空，直接根据value查询
        if(StringUtils.isEmpty(dictCode)) {
            //直接根据value查询
            QueryWrapper<Dict> wrapper = new QueryWrapper<>();
            wrapper.eq("value",value);
            Dict dict = baseMapper.selectOne(wrapper);
            return dict.getDictName();
        } else {//如果dictCode不为空，根据dictCode和value查询
            //根据dictcode查询dict对象，得到dict的id值
            Dict codeDict = this.getDictByDictCode(dictCode);
            Long parent_id = codeDict.getId();
            //根据parent_id和value进行查询
            Dict finalDict = baseMapper.selectOne(new QueryWrapper<Dict>()
                    .eq("parent_id", parent_id)
                    .eq("value", value));
            return finalDict.getDictName();
        }
    }
    private Dict getDictByDictCode(String dictCode) {
        QueryWrapper<Dict> wrapper = new QueryWrapper<>();
        wrapper.eq("dict_code",dictCode);
        Dict codeDict = baseMapper.selectOne(wrapper);
        return codeDict;
    }
    @Override
    public List<Dict> findByDictCode(String dictCode) {
        //根据dictcode获取对应id
        Dict dict = this.getDictByDictCode(dictCode);
        //根据id获取子节点
        List<Dict> chlidData = this.findByIdData(dict.getId());
        return chlidData;
    }

}
