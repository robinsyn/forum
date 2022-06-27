package com.example.forum;

import com.example.forum.entity.DiscussPost;
import com.example.forum.entity.LoginTicket;
import com.example.forum.entity.User;
import com.example.forum.mapper.DiscussPostMapper;
import com.example.forum.mapper.LoginTicketMapper;
import com.example.forum.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.List;

@SpringBootTest
public class MapperTests {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Test
    void testSelectUser(){
        User user = userMapper.selectById(11);
        System.out.println(user);

        user=userMapper.selectByName("nowcoder11");
        System.out.println(user);

        user=userMapper.selectByEmail("nowcoder11@sina.com");
        System.out.println(user);
    }

    @Test
    public void testInsert(){
        User user=new User();
        user.setUsername("hhh");
        user.setPassword("111222");
        user.setCreateTime(new Date());
        user.setHeaderUrl("http://images.nowcoder.com/head/10.png");

        int rows = userMapper.insertUser(user);
        System.out.println(rows);
        System.out.println(user.getId());
    }

    @Test
    public void testUpdate(){
        int rows = userMapper.updateHeader(151, "http://images.nowcoder.com/head/100.png");
        System.out.println(rows);

        rows= userMapper.updatePassword(151, "000");
        System.out.println(rows);

        rows=userMapper.updateStatus(151,1);
        System.out.println(rows);
    }

    @Test
    public void testDiscussport(){
        List<DiscussPost> list = discussPostMapper.selectDiscussPosts(149, 0, 10,0);
        for (DiscussPost discussPost : list) {
            System.out.println(discussPost);
        }

        int i = discussPostMapper.selectDiscussPostRows(149);
        System.out.println(i);
    }

    @Test
    public void testInsertLoginTicket(){
        LoginTicket loginTicket=new LoginTicket();
        loginTicket.setUserId(101);
        loginTicket.setStatus(0);
        loginTicket.setTicket("qwe");
        loginTicket.setExpired(new Date(System.currentTimeMillis()+1000*60*10));
        int i = loginTicketMapper.insertLoginTicket(loginTicket);
        System.out.println(i);
    }

    @Test
    public void testUpdateLoginTicket(){
        LoginTicket loginTicket = loginTicketMapper.selectByTicket("qwe");
        System.out.println(loginTicket);

        int i = loginTicketMapper.updateStatus("qwe", 1);
        System.out.println(i);
    }
}
