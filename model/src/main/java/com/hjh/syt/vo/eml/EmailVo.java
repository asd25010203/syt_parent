package com.hjh.syt.vo.eml;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailVo {
    private String mailNumber;
    private Map<String,Object> param;

}
