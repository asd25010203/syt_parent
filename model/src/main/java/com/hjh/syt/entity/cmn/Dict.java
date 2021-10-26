package com.hjh.syt.entity.cmn;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: hasee
 * @date: 2021/6/20 22:06
 * @description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "数据字典")
@TableName("dict")
public class Dict implements Serializable {
    @ApiModelProperty(value = "id")
    @TableId("id")
    private Long id;
    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "create_time")
    private Date createTime;
    @ApiModelProperty(value = "修改时间")
    @TableField(value = "update_time")
    private Date updateTime;
    @ApiModelProperty(value = "逻辑删除：0为未删除，1为删除")
    @TableField(value = "is_deleted")
    @TableLogic //开启逻辑删除
    private Integer isDeleted;
    @ApiModelProperty(value = "其他参数")
    @TableField(exist = false)
    private Map<String,Object> Param = new HashMap<>();
    @ApiModelProperty("上级id")
    @TableField("parent_id")
    private Long parentId;
    @ApiModelProperty("名称")
    @TableField("dict_name")
    private String dictName;
    @ApiModelProperty("值")
    @TableField("value")
    private String value;
    @ApiModelProperty("编号")
    @TableField("dict_code")
    private String dictCode;
    @ApiModelProperty("是否包含子节段")
    @TableField(exist = false)
    private boolean hasChildren;
}
