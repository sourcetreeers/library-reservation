package com.library.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.library.entity.Library;
import com.library.vo.PageResult;

/**
 * 图书馆服务接口
 */
public interface LibraryService extends IService<Library> {
    
    /**
     * 分页查询图书馆
     */
    PageResult<Library> pageQuery(int current, int size, String name);
}