package com.example.forum.interceptor;

import com.example.forum.entity.User;
import com.example.forum.service.DataService;
import com.example.forum.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class DataInterceptor implements HandlerInterceptor {

    @Autowired
    private DataService dataService;

    @Autowired
    private HostHolder hostHolder;

    //在请求之前拦截，只是记录数据，所以要返回true，让其继续向下执行
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //统计单日的UV
        //通过request得到ip
        String ip = request.getRemoteHost();
        dataService.recordUV(ip);//调用service统计

        //统计单日的DAU
        //得到当前用户，并且判断登陆后才记录
        User user = hostHolder.getUser();
        if (user!=null){
            dataService.recordDAU(user.getId());
        }
        return true;
    }
}
