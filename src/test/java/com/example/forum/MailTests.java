package com.example.forum;

import com.example.forum.util.MailClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@SpringBootTest
public class MailTests {
    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;//模版引擎

    @Test
    public void testMail(){
        mailClient.sendMail("luoyang0531@163.com","来自困困小狗","hello");

    }

    @Test
    public void testHtmlMail(){
        //生成动态网页
        Context context=new Context();
        context.setVariable("username","haohao");

        String content = templateEngine.process("/mail/demo", context);
        System.out.println(content);

        //发送邮件
        mailClient.sendMail("869083646@qq.com","Test2",content);
    }
}
