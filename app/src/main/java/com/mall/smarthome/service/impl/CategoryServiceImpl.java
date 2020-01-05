package com.mall.smarthome.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mall.smarthome.common.ServerResponse;
import com.mall.smarthome.dao.CategoryMapper;
import com.mall.smarthome.pojo.Category;
import com.mall.smarthome.service.ICategoryService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;

@Service("iCategoryService")
public class CategoryServiceImpl implements ICategoryService {

    private Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);

    @Autowired
    private CategoryMapper categoryMapper;

    public ServerResponse addCategory(String categoryName, Integer parentId){
        if(parentId == null || StringUtils.isBlank(categoryName)){
            return ServerResponse.createByErrorMessage("the added category parameter is wrong");
        }

        Category category = new Category();
        category.setName(categoryName);
        category.setParentId(parentId);
        category.setStatus(true); // this category is available

        int rowCount = categoryMapper.insert(category);
        if (rowCount > 0){
            return  ServerResponse.createBySuccess("add category successfully");
        }

        return ServerResponse.createByErrorMessage("fail to add category");
    }

    public ServerResponse setCategoryName(Integer categoryId, String categoryName){
        if(categoryId == null || StringUtils.isBlank(categoryName)){
            return ServerResponse.createByErrorMessage("category id is wrong");
        }

        Category category = new Category();
        category.setId(categoryId);
        category.setName(categoryName);

        int rowCount = categoryMapper.updateByPrimaryKey(category);
        if (rowCount > 0){
            return  ServerResponse.createBySuccess("update category name successfully");
        }

        return ServerResponse.createByErrorMessage("fail to update category name");
    }

    public ServerResponse<List<Category>> getChildrenParallelCategory(Integer categoryId){
        List<Category> categoryList = categoryMapper.selectCategoryChildrenByParentId(categoryId);
        if (CollectionUtils.isEmpty(categoryList)){
            logger.info("can't find the sub category from the current category");
        }

        return ServerResponse.createBySuccess(categoryList);
    }

    public ServerResponse selectCategoryAndChildrenById(Integer categoryId){
        Set<Category> categorySet = Sets.newHashSet();
        findChildCategory(categorySet, categoryId);

        List<Integer> categoryIdList = Lists.newArrayList();
        if(categoryId != null){
            for (Category categoryItem: categorySet){
                categoryIdList.add(categoryItem.getId());
            }
        }

        return ServerResponse.createBySuccess(categoryIdList);
    }

    //recursion of category
    private Set<Category> findChildCategory(Set<Category> categorySet, Integer categoryId){
        Category category = categoryMapper.selectByPrimaryKey(categoryId);

        if (category != null){
            categorySet.add(category);
        }

        List<Category> categoryList = categoryMapper.selectCategoryChildrenByParentId(categoryId);
        for(Category categoryItem: categoryList){
            findChildCategory(categorySet, categoryItem.getId());
        }

        return categorySet;
    }
}
