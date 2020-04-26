package com.baidu.speech.myapplication;

import java.io.Serializable;

public class Item implements Serializable {
    public String imgPath;
    public String title;
    public Integer id;

    public Item(){

    }
    public Item(String imgPath,String title,Integer id){
        this.imgPath = imgPath;
        this.title = title;
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getImgPath(){
        return imgPath;
    }
    public void setImgPath(String imgPath){
        this.imgPath = imgPath;
    }
    public String getTitle(){
        return title;
    }
    public void setTitle(String title){
        this.title = title;
    }


}
