package com.library.service;

import com.library.dto.DisputeQuery;
import com.library.dto.DisputeVO;
import com.library.dto.HandleDisputeDTO;
import com.library.vo.PageResult;

/**
 * 争议统一处理服务接口
 */
public interface DisputeService {

    /**
     * 分页查询所有争议（合并申诉和举报）
     */
    PageResult<DisputeVO> getDisputePage(DisputeQuery query);

    /**
     * 处理争议（根据类型分发）
     */
    void handleDispute(Long id, Long handlerId, HandleDisputeDTO dto);
}
