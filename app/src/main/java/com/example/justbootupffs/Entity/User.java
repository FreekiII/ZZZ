package com.example.justbootupffs.Entity;

import java.util.HashMap;
import java.util.LinkedList;

//POJO Class for users
public class User {
    public String id, email, password, name, surname, age, profilePicture, description;
    public HashMap<String, String> roles;

    public User() {
    }

    public User(String id, String name, String surname, String age, String email, String password) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.surname = surname;
        this.age = age;
        profilePicture = "";
        description = "";
        roles = new HashMap<>();
        roles.put("USER", "1");
    }
}
