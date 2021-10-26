package com.hjh.syt.vo.hosp;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "Schedule")
public class ScheduleQueryVo {
	
	@ApiModelProperty(value = "医院编号")
	private String hospCode;

	@ApiModelProperty(value = "科室编号")
	private String depCode;

	@ApiModelProperty(value = "医生编号")
	private String docCode;

	@ApiModelProperty(value = "安排日期")
	private Date workDate;

	@ApiModelProperty(value = "安排时间（0：上午 1：下午）")
	private Integer workTime;

}

