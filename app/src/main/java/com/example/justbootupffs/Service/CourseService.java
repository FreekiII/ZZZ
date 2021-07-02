package com.example.justbootupffs.Service;

import com.example.justbootupffs.Entity.Course;
import com.example.justbootupffs.Entity.Message;

public class CourseService {
    private Course course;

    private final static String set = "1";

    public CourseService(){
    }

    public CourseService(Course course) {
        this.course = course;
    }

    public void addMentor(String id) {
        this.course.mentors.put(id, set);
    }

    public void addStudent(String id) {
        this.course.students.put(id, set);
    }

    public void setDescription(String description) {
        this.course.description = description;
    }

    public void setTeacher(String teacher) {
        this.course.teacher = teacher;
    }

    public String getDescription() {
        return this.course.description;
    }

    public String getTeacher() {
        return this.course.teacher;
    }
}
