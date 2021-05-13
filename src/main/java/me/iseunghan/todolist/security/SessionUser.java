package me.iseunghan.todolist.security;

import me.iseunghan.todolist.model.User;

import java.io.Serializable;

public class SessionUser implements Serializable {
    private String name;
    private String email;
    private String image;

    public SessionUser(User user) {
        this.name = user.getName();
        this.email = user.getEmail();
        this.image = user.getImage();
    }

    public SessionUser() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
