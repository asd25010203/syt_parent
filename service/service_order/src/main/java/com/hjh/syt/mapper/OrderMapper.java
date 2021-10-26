package com.hjh.syt.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hjh.syt.entity.order.OrderInfo;
import com.hjh.syt.vo.order.OrderCountQueryVo;
import com.hjh.syt.vo.order.OrderCountVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderMapper extends BaseMapper<OrderInfo> {

    //查询预约统计数据的方法
    List<OrderCountVo> selectOrderCount(@Param("vo") OrderCountQueryVo orderCountQueryVo);
}
