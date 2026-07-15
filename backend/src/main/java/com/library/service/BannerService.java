package com.library.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.library.entity.Banner;
import java.util.List;

/**
 * 轮播图Service
 */
public interface BannerService extends IService<Banner> {
    
    /**
     * 获取启用的轮播图列表（按排序排序）
     */
    List<Banner> getActiveBanners();
}
