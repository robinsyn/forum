package com.example.forum.controller;


import com.example.forum.annotation.LoginRequired;
import com.example.forum.entity.User;
import com.example.forum.service.FollowService;
import com.example.forum.service.LikeService;
import com.example.forum.service.UserService;
import com.example.forum.util.CommunityConstant;
import com.example.forum.util.CommunityUtil;
import com.example.forum.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;


@Controller
@RequestMapping("/user")
public class UserController implements CommunityConstant {

    private static final Logger logger= LoggerFactory.getLogger(UserController.class);

    @Value("${community.path.domain}")
    private String domain;

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private FollowService followService;

    /*在登录后才能访问，加上注解拦截*/
    @LoginRequired
    @RequestMapping(path = "/setting",method = RequestMethod.GET)
    public String getSettingPage(){
        return "/site/setting";
    }

    /*在登录后才能访问，加上注解拦截*/
    @LoginRequired
    @RequestMapping(path = "/upload",method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model){
        //需要model，将数据从模版上直接获取
        //空值判断
        if(headerImage==null){
            //文件不存在，返回到设置页
            model.addAttribute("error","您没有选择图片！");
            return "/site/setting";
        }

        //文件存在，生成随机字符串命名，文件的后缀需要从文件名获取

        //从最后一个。开始截取
        String fileName = headerImage.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        //获得后缀名后先判断一下后缀是否合理（不为空）
        if(StringUtils.isBlank(suffix)){
            model.addAttribute("error","文件的格式不正确！");
        }
        //生成随机文件名
        fileName = CommunityUtil.generateUUID()+"."+suffix;

        //确定文件存放的路径
        File dest=new File(uploadPath+"/"+fileName);

        //存储文件，将当前内容写入到文件里面
        try {
            headerImage.transferTo(dest);
        } catch (IOException e) {
            //抛出异常，打断下面程序的执行
            logger.error("上传文件失败："+e.getMessage());
            throw new RuntimeException("上传文件失败，服务器异常！",e);
        }

        //更新当前用户的头像路径，web访问路径
        //http://localhost:8080/forum/user/header/xxx.png
        //获取当前用户
        User user = hostHolder.getUser();
        //设置头像路径
        String headerUrl=domain+contextPath+"/user/header/"+fileName;

        //更新头像
        userService.updateHeaderUrl(user.getId(),headerUrl);

        // 重定向到首页
        return "redirect:/index";
    }

    @RequestMapping(path = "/header/{fileName}",method = RequestMethod.GET)
    public void getHeader(@PathVariable("fileName")String fileName, HttpServletResponse response){
        //服务器存放的路径
        fileName = uploadPath + "/" + fileName;
        //文件后缀
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);

        /*
        响应图片
        输入流我们自己创建的，需要我们自己去关闭，输出流是spring托管，
        写在try的小括号里，会自己添加到finally里面，关闭

        * */
        //声明输出的格式
        response.setContentType("image/"+suffix);
        //返回二进制字节流，获得输出流
        try(
                OutputStream os = response.getOutputStream();
                FileInputStream fis=new FileInputStream(fileName);
        ) {
            //读文件，利用缓冲区读取，游标去读取
            byte[] buffer=new byte[1024];
            int b=0;
            while((b=fis.read(buffer))!=-1){
                os.write(buffer,0,b);
            }

        } catch (IOException e) {
            logger.error("读取头像失败："+e.getMessage());
        }
    }


    //个人主页
    @RequestMapping(path = "/profile/{userId}",method = RequestMethod.GET)
    public String getProfilePage(@PathVariable("userId")int userId,Model model){
        //得到user
        User user = userService.findUserById(userId);
        //判断用户是否存在
        if(user==null){
            throw new RuntimeException("该用户不存在！");
        }
        //将用户传给页面
        model.addAttribute("user",user);
        //获得该用户被点赞的数量
        int likeCount = likeService.findUserLikeCount(userId);
        //将数据发给页面
        model.addAttribute("likeCount",likeCount);

        //查询关注数量
        long followeeCount = followService.findFolloweeCount(userId, ENTITY_TYPE_USER);
        model.addAttribute("followeeCount",followeeCount);
        //粉丝数量
        long followerCount = followService.findFollowerCount(ENTITY_TYPE_USER, userId);
        model.addAttribute("followerCount",followerCount);

        //当前登录用户对某用户是否已关注
        //先判断当前用户是否登录
        boolean hasFollowed=false;
        if(hostHolder.getUser()!=null){
            hasFollowed = followService.hasFollowed(hostHolder.getUser().getId(), ENTITY_TYPE_USER, userId);
        }
        model.addAttribute("hasFollowed",hasFollowed);


        return "/site/profile";
    }
}
