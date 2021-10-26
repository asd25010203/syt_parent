package com.hjh.syt.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hjh.syt.entity.cmn.Dict;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.List;

/**
 * @author: hasee
 * @date: 2021/6/20 22:19
 * @description:
 */
public interface DictService extends IService<Dict> {
    public List<Dict> findByIdData(Long parentId);
    public void ReadDict(HttpServletResponse response);
    public void WriterDict(MultipartFile file);
    //根据dictcode和value查询
    String getDictName(String dictCode, String value);
    //根据dictCode获取下级节点
    List<Dict> findByDictCode(String dictCode);
}
