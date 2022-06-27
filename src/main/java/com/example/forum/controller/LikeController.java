package com.example.forum.controller;

import com.example.forum.entity.Event;
import com.example.forum.entity.User;
import com.example.forum.event.EventProducer;
import com.example.forum.service.LikeService;
import com.example.forum.util.CommunityConstant;
import com.example.forum.util.CommunityUtil;
import com.example.forum.util.HostHolder;
import com.example.forum.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;


@Controller
public class LikeController implements CommunityConstant {
    @Autowired
    private LikeService likeService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private EventProducer eventProducer;


    //异步请求点赞
    @RequestMapping(path = "/like",method = RequestMethod.POST ) //点赞需要传入一些信息进来所以是post
    @ResponseBody //异步请求
    public String like(int entityType,int entityId, int entityUserId, int postId){
        //获得当前用户
        User user = hostHolder.getUser();
        if(user==null){
            //返回json格式的提示
            return CommunityUtil.getJSIONString(1,"登录后才可点赞");
        }
        //实现点赞功能
        likeService.like(user.getId(),entityType,entityId,entityUserId);
        //获得点赞数量，状态
        long likeCount = likeService.findEntityLikeCount(entityType, entityId);
        int likeStatus = likeService.findEntityLikeStatus(user.getId(), entityType, entityId);
        //map封装信息（点赞数量，状态），传给页面
        Map<String,Object> map=new HashMap<>();
        map.put("likeCount",likeCount);
        map.put("likeStatus",likeStatus);

        //封装点赞事件，链接点赞帖子详情
        if(likeStatus == 1) {
            Event event=new Event()
                    .setTopic(TOPIC_LIKE)
                    .setUserId(hostHolder.getUser().getId())//事件触发者
                    .setEntityType(entityType)
                    .setEntityId(entityId)
                    .setEntityUserId(entityUserId)//帖子的作者
                    .setData("postId",postId);//将贴子id存到map集合里
            eventProducer.fireEvent(event);
        }

        //先判断是对帖子点赞才将贴子放到redis里面
        if(entityType==ENTITY_TYPE_POST){
            //在点赞帖子后面也计算分数，并将其存到redis里面
            String redisKey = RedisKeyUtil.getPostScoreKey();  //得到key
            redisTemplate.opsForSet().add(redisKey,postId);//将贴子存到set集合中
        }

        //返回json格式的提示
        return CommunityUtil.getJSIONString(0,null,map);

    }


}
