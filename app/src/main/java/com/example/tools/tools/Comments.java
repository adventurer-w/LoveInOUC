package com.example.tools.tools;

import java.util.List;

public class Comments {


    private int comment_num;
    private int like_num;
    private String create_time;
    private int star_num;
    private int tag;
    private int author_id;
    private String title,writer,photo,first;
    private List<String> pics;
    private String content,comment_writer,comment_content,error,noComments,info;
    private Boolean follow;


    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getFirst() {
        return first;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    public void setFollow(Boolean follow) {
        this.follow = follow;
    }

    public Boolean getFollow() {
        return follow;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getPhoto() {
        return photo;
    }

    public String getWriter() {
        return writer;
    }

    public String getNoComments() {
        return noComments;
    }

    public void setNoComments(String noComments) {
        this.noComments = noComments;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public void setComment_writer(String comment_writer) {
        this.comment_writer = comment_writer;
    }

    public void setComment_content(String comment_content) {
        this.comment_content = comment_content;
    }

    public String getComment_writer() {
        return comment_writer;
    }

    public String getComment_content() {
        return comment_content;
    }

    public void setComment_num(int comment_num) {
        this.comment_num = comment_num;
    }

    public void setLike_num(int like_num) {
        this.like_num = like_num;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public void setStar_num(int star_num) {
        this.star_num = star_num;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public void setAuthor_id(int author_id) {
        this.author_id = author_id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPics(List<String> pics) {
        this.pics = pics;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getComment_num() {
        return comment_num;
    }

    public int getLike_num() {
        return like_num;
    }

    public String getCreate_time() {
        return create_time;
    }

    public int getStar_num() {
        return star_num;
    }

    public int getTag() {
        return tag;
    }

    public int getAuthor_id() {
        return author_id;
    }

    public String getTitle() {
        return title;
    }

    public List<String> getPics() {
        return pics;
    }

    public String getContent() {
        return content;
    }
}
