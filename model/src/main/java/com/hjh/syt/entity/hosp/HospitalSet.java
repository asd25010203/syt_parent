package com.hjh.syt.entity.hosp;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import com.hjh.syt.entity.base.BeanEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author: hasee
 * @date: 2021/6/14 21:24
 * @description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel(description = "医院设置")
@TableName("hospital_set")
public class HospitalSet extends BeanEntity {
    @ApiModelProperty(value = "医院名称")
    @TableField("hosp_name")
    private String hospName;
    @ApiModelProperty(value = "医院编号")
    @TableField("hosp_code")
    private String hospCode;
    @ApiModelProperty(value = "api基本路径")
    @TableField(value = "api_url")
    private String apiUrl;
    @ApiModelProperty(value = "签名秘钥")
    @TableField(value = "sign_key")
    private String signKey;
    @ApiModelProperty(value = "联系人名字")
    @TableField("contacts_name")
    private String contactsName;
    @ApiModelProperty(value = "联系人手机")
    @TableField("contacts_phone")
    private String contactsPhone;
    @ApiModelProperty(value = "状态")
    @TableField(value = "status")
    private Integer status;
    @ApiModelProperty(value = "版本")
    @TableField(value = "version",fill = FieldFill.INSERT)
    @Version
    private Integer version;
}
