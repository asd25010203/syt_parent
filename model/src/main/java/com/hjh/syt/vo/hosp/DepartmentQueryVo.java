package com.hjh.syt.vo.hosp;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "Department")
public class DepartmentQueryVo {
	
	@ApiModelProperty(value = "医院编号")
	private String hospCode;

	@ApiModelProperty(value = "科室编号")
	private String depCode;

	@ApiModelProperty(value = "科室名称")
	private String depName;

	@ApiModelProperty(value = "大科室编号")
	private String bigCode;

	@ApiModelProperty(value = "大科室名称")
	private String bigName;

}

