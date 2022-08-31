package com.example.tools.tools;

import java.util.List;

public class Data {
    /**
     * pics : ["http://122.9.2.27/media/up_image/image-20210303144726687.png","http://122.9.2.27/media/up_image/image-20210303144658311.png","http://122.9.2.27/media/up_image/image-20210303144658311.png","http://122.9.2.27/media/up_image/0.jpg"]
     */
    //轮播图

    /**
     * news_pics_set : []
     *      * like_num : 0
     *      * create_time : 2021-03-20T11:16:30.679320+08:00
     *      * star_num : 0
     *      * id : 4
     *      * title : asdasdasdasdasdas
     *      * content : dasdasdasdasdasdas
     *      * status : 1
     */
    private List<String> pics;
    private String news_pics_set;
    private int tag;
    private String create_time;
    private int star_num;
    private int news_id,writer_id;
    private String title;
    private String content;
    private int status;
    private String writer;
    private String error,noData;
    private String photo,info;


    public String getNoData() {
        return noData;
    }

    public void setNoData(String noData) {
        this.noData = noData;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public int getTag() {
        return tag;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getPhoto() {
        return photo;
    }

    public int getWriter_id() {
        return writer_id;
    }

    public void setWriter_id(int writer_id) {
        this.writer_id = writer_id;
    }

    public void setError(String error) {
        this.error = error;
    }

    public void setPics(List<String> pics) {
        this.pics = pics;
    }

    public List<String> getPics() {
        return pics;
    }

    public void setNews_pics_set(String news_pics_set) {
        this.news_pics_set = news_pics_set;
    }


    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public void setStar_num(int star_num) {
        this.star_num = star_num;
    }

    public void setNews_Id(int news_id) {
        this.news_id = news_id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    ////////////////////////////////////////////////////////////////

    public String getError() {
        return error;
    }

    public String getNews_pics_set() {
        return news_pics_set;
    }



    public String getCreate_time() {
        return create_time;
    }

    public int getStar_num() {
        return star_num;
    }

    public int getNew_Id() {
        return news_id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public int getStatus() {
        return status;
    }

    public String getWriter() {
        return writer;
    }
}
