package com.example.linson.notepad.domain;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * Created by linson on 2017/1/9.
 */

public class ContentBean extends DataSupport implements Serializable{
    private int id;
    private String title;
    private String content;
    private String update;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUpdate() {
        return update;
    }

    public void setUpdate(String update) {
        this.update = update;
    }

    @Override
    public String toString() {
        return "ContentBean{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", update=" + update +
                '}';
    }
}
