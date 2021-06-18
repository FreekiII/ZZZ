package com.example.justbootupffs.Entity;

import android.net.Uri;

//POJO Class for users
public class User {
    public String id, username, email, password, description, profilePicture;

    public User() {
    }

    public User(String id, String username, String email, String password) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        profilePicture = "";
        description = "";
    }
}
