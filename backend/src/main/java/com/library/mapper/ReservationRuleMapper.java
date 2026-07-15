package com.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.library.entity.ReservationRule;
import org.apache.ibatis.annotations.Mapper;

/**
 * 预约规则Mapper
 */
@Mapper
public interface ReservationRuleMapper extends BaseMapper<ReservationRule> {
}
