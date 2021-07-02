package com.example.justbootupffs.Entity;

import java.util.HashMap;

//POJO Class for courses
public class Course {
    public String description, teacher;
    public HashMap<String, String> students, mentors;

    public Course() {
    }

    public Course(String description, String teacher) {
        this.description = description;
        this.teacher = teacher;
    }
}
