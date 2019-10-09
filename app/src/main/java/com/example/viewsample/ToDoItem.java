package com.example.viewsample;

// ToDoの各属性をまとめて保持するくらす
public class ToDoItem {
    private long id;
    private String name;
    private String detail;
    private String timeStamp;
    private String isStar;  // "0":false, "1":true
    private String isArchive; // "0":false, "1":true

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

    public String getIsStar() { return isStar;}

    public void setIsStar(String isStar) { this.isStar = isStar; }

    public String getIsArchive() { return isArchive; }

    public void setIsArchive(String isArchive) { this.isArchive = isArchive; }
}
