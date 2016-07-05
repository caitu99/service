package com.caitu99.service.goods.dao;

import java.util.List;
import java.util.Map;

import com.caitu99.service.goods.domain.Template;
import com.caitu99.service.goods.dto.TemplateDto;

public interface TemplateMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Template record);

    int insertSelective(Template record);

    Template selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Template record);

    int updateByPrimaryKey(Template record);
    
    
    
    Integer selectPageCount(Map<String, Object> map);
    
    List<TemplateDto> selectPageList(Map<String, Object> map);
    
}