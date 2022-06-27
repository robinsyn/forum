package com.example.forum.controller;

import com.example.forum.entity.DiscussPost;
import com.example.forum.entity.Page;
import com.example.forum.entity.User;
import com.example.forum.service.DiscussPostService;
import com.example.forum.service.LikeService;
import com.example.forum.service.UserService;
import com.example.forum.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController implements CommunityConstant {

    @Autowired
    private UserService userService;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private LikeService likeService;

    @RequestMapping(path = "/index",method = RequestMethod.GET)
    public String getIndexPage(Model model, Page page,
                               @RequestParam(name = "orderMode",defaultValue = "0") int orderMode){
        /*方法调用前，SpringMVC会自动实例化Model和Page，并将Page注入到Model，
        所以在thymeleaf中可以直接访问Page对象里面的数据*/
        //设置Page类需要的信息
        page.setRows(discussPostService.findDiscussPostRows(0));//首页不需要userid
        page.setPath("/index?orderMode=" + orderMode);//分页设置要根据orderMode来区分

        //获得帖子集合,从Page中获取分页信息
        List<DiscussPost>  list= discussPostService.findDiscussPosts(0, page.getoffset(), page.getLimit(),orderMode);
        //将帖子和用户都存到该集合中
        List<Map<String,Object>> discussPosts=new ArrayList<>();

        if(list!=null){
            //遍历集合
            for (DiscussPost post : list) {
                //map里面存两类，一个post帖子，一个user
                Map<String,Object> map=new HashMap<>();
                map.put("post",post);
                User user = userService.findUserById(post.getUserId());
                map.put("user",user);
                //查询帖子赞的数量
                long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId());
                map.put("likeCount",likeCount);
                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts",discussPosts);
        model.addAttribute("orderMode",orderMode);//将按照什么排行存到model里面，方便前端获取
        return "/index";
    }

    @RequestMapping(path = "/error",method = RequestMethod.GET)
    public String getErrorPage(){
        return "/error/500";
    }
}
