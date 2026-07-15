package com.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.library.entity.Library;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 图书馆Mapper接口
 */
@Mapper
public interface LibraryMapper extends BaseMapper<Library> {
    
    /**
     * 分页查询图书馆
     */
    List<Library> selectPageWithCondition(@Param("name") String name, 
                                         @Param("offset") int offset, 
                                         @Param("size") int size);
    
    /**
     * 查询总数
     */
    long countWithCondition(@Param("name") String name);
}