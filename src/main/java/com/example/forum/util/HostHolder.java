package com.example.forum.util;

import com.example.forum.entity.User;
import org.springframework.stereotype.Component;


/*持有用户的信息，多线程隔离，用于代替session对象*/
@Component
public class HostHolder {

    private ThreadLocal<User> users=new ThreadLocal<>();

    public void setUser(User user){
        users.set(user);
    }

    public User getUser(){
        return users.get();
    }

    //清理
    public void clear(){
        users.remove();
    }
}
