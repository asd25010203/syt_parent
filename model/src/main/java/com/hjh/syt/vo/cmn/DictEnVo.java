package com.hjh.syt.vo.cmn;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: hasee
 * @date: 2021/6/20 22:06
 * @description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "数据字典")
public class DictEnVo {
    @ApiModelProperty("id")
    private Long id;
    @ApiModelProperty("上级id")
    private Long parentId;
    @ApiModelProperty("名称")
    private String dictName;
    @ApiModelProperty("值")
    private String value;
    @ApiModelProperty("编号")
    private String dictCode;
}
