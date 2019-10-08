package com.example.viewsample;

// ToDoの各属性をまとめて保持するくらす
public class ToDoItem {
    private long id;
    private String name;
    private String detail;
    private String timeStamp;

    // getterとsetter すべてpublic
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getTimeStamp() { return timeStamp; }

    public void setTimeStamp(String timeStamp) { this.timeStamp = timeStamp; }
}
