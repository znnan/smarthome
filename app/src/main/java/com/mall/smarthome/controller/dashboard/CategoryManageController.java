package com.mall.smarthome.controller.dashboard;

import com.mall.smarthome.common.Const;
import com.mall.smarthome.common.ResponseCode;
import com.mall.smarthome.common.ServerResponse;
import com.mall.smarthome.pojo.User;
import com.mall.smarthome.service.ICategoryService;
import com.mall.smarthome.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/manage/category")
public class CategoryManageController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private ICategoryService iCategoryService;

    @RequestMapping(value = "add_category.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse addCategory(HttpSession session, String categoryName, @RequestParam(value = "parentId", defaultValue = "0") int parentId){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "user doesn't log in");
        }

        if (iUserService.checkAdminRole(user).isSuccess()){
            return iCategoryService.addCategory(categoryName, parentId);
        }

        return ServerResponse.createByErrorMessage("Need admin permission");
    }

    @RequestMapping(value = "set_category_name.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse setCategoryName(HttpSession session, Integer categoryId, String categoryName){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "user doesn't log in");
        }

        if (iUserService.checkAdminRole(user).isSuccess()){
            return iCategoryService.setCategoryName(categoryId, categoryName);
        }

        return ServerResponse.createByErrorMessage("Need admin permission");
    }

    @RequestMapping(value = "get_category.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse getChildrenParallelCategory(HttpSession session, @RequestParam(value = "categoryId", defaultValue = "0") Integer categoryId){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "user doesn't log in");
        }

        if (iUserService.checkAdminRole(user).isSuccess()){
            return iCategoryService.getChildrenParallelCategory(categoryId);
        }

        return ServerResponse.createByErrorMessage("Need admin permission");
    }

    @RequestMapping(value = "get_sub_categories.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse getCategoryAndSubChildrenCategories(HttpSession session, @RequestParam(value = "categoryId", defaultValue = "0") Integer categoryId){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "user doesn't log in");
        }

        if (iUserService.checkAdminRole(user).isSuccess()){
            return iCategoryService.selectCategoryAndChildrenById(categoryId);
        }

        return ServerResponse.createByErrorMessage("Need admin permission");
    }
}
