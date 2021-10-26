package com.hjh.syt.vo.hosp;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: hasee
 * @date: 2021/6/15 16:56
 * @description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HospitalSetQueryVo {
    @ApiModelProperty(value = "医院名称")
    private String hospName;
    @ApiModelProperty(value = "医院编号")
    private String hospCode;
}
