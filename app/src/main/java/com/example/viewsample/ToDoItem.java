package com.example.viewsample;

public class ToDoItem {
    private long id;
    private String name;
    private String detail;
    // String timeStamp;


    /*
     * getter と setter
     */
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
}
