package ru.practicum.ewm.Comments.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.ewm.Comments.Comment;
import ru.practicum.ewm.utils.Utils;

import java.util.List;

@Mapper(componentModel = "spring", uses = {Utils.class})
public interface CommentMaper {

    @Mapping(target = "user", expression="java(comment.getUser().getId())")
    @Mapping(target = "event", expression="java(comment.getEvent().getId())")
    CommentDto toCommentDto(Comment comment);

    Comment toComment(CommentDto commentDto);

    List<CommentDto> toListCommentDto(List<Comment> categoryList);

}
