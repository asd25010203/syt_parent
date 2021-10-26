package com.hjh.syt.entity.base;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
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
 * @date: 2021/6/14 22:44
 * @description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BeanEntity implements Serializable { //反序序列
    @ApiModelProperty(value = "id")
    @TableId(type = IdType.AUTO)
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
}
