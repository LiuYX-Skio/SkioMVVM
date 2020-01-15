package com.skio.entity;

/**
 * @Description:
 * @Author: ch
 * @CreateDate: 2019/7/23 15:53
 */
public class Title {
    private String title;
    private String city;
    private int age;

    public Title() {
    }

    public Title(String title, String city, int age) {
        this.title = title;
        this.city = city;
        this.age = age;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
