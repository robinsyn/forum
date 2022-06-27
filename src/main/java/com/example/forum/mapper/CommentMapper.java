package com.example.forum.mapper;

import com.example.forum.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentMapper {
    //根据评论类型分页查询
    List<Comment> selectCommentByEntity(int entityType, int entityId, int offset, int limit);

    //查询总评论数
    int selectCountByEntity(int entityType,int entityId);

    //增加评论数据
    int insertComment(Comment comment);

    //根据回复id查找回复
    Comment selectCommentById(int id);

    //根据用户id分页查询该用户的所有回复集合
    List<Comment> selectCommentsByUser(int userId,int offset,int limit);

    //根据用户id查找回复总数
    int selectCountByUser(int userId);



}
