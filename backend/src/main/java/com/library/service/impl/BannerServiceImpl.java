package com.library.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.library.entity.Banner;
import com.library.mapper.BannerMapper;
import com.library.service.BannerService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 轮播图Service实现
 */
@Service
public class BannerServiceImpl extends ServiceImpl<BannerMapper, Banner> implements BannerService {
    
    @Override
    public List<Banner> getActiveBanners() {
        LambdaQueryWrapper<Banner> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Banner::getStatus, "启用")
               .orderByAsc(Banner::getSortOrder) // 按排序升序
               .orderByAsc(Banner::getCreateTime); // 再按创建时间升序
        
        return list(wrapper);
    }
}
