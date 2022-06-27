package com.example.forum.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)//在方法上有效
@Retention(RetentionPolicy.RUNTIME) //在程序运行时有效
public @interface LoginRequired {

}
