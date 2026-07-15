package com.library.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.library.entity.Library;
import com.library.mapper.LibraryMapper;
import com.library.service.LibraryService;
import com.library.vo.PageResult;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 图书馆服务实现类
 */
@Service
public class LibraryServiceImpl extends ServiceImpl<LibraryMapper, Library> implements LibraryService {
    
    @Override
    public PageResult<Library> pageQuery(int current, int size, String name) {
        // 计算偏移量
        int offset = (current - 1) * size;
        
        // 查询数据
        List<Library> records = baseMapper.selectPageWithCondition(name, offset, size);
        
        // 查询总数
        long total = baseMapper.countWithCondition(name);
        
        // 返回分页结果
        return new PageResult<>(records, total, current, size);
    }
}