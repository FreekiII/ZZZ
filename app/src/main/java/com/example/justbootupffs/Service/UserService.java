package com.example.justbootupffs.Service;

import com.example.justbootupffs.Entity.User;

public class UserService {
    private User user;

    private final static String privilegesAdmin = "ADMIN";
    private final static String privilegesTeacher = "TEACHER";
    private final static String privilegesStudent = "STUDENT";
    private final static String privilegesMentor = "MENTOR";
    private final static String set = "1";

    public UserService() {
    }

    public UserService(User user) {
        this.user = user;
    }

    public void setPrivilegesTeacher() {
        if (!user.roles.containsKey(privilegesTeacher)) {
            user.roles.put(privilegesTeacher, set);
        }
    }

    public void removePrivilegesTeacher() {
        if (user.roles.containsKey(privilegesTeacher)) {
            user.roles.remove(privilegesTeacher);
        }
    }

    public void setPrivilegesMentor() {
        if (!user.roles.containsKey(privilegesMentor)) {
            user.roles.put(privilegesMentor, set);
        }
    }

    public void removePrivilegesMentor() {
        if (user.roles.containsKey(privilegesMentor)) {
            user.roles.remove(privilegesMentor);
        }
    }

    public void setPrivilegesStudent() {
        if (!user.roles.containsKey(privilegesStudent)) {
            user.roles.put(privilegesStudent, set);
        }
    }

    public void removePrivilegesStudent() {
        if (user.roles.containsKey(privilegesStudent)) {
            user.roles.remove(privilegesStudent);
        }
    }

    public void setPrivilegesAdmin() {
        if (!user.roles.containsKey(privilegesAdmin)) {
            user.roles.put(privilegesAdmin, set);
        }
    }

    public void removePrivilegesAdmin() {
        if (user.roles.containsKey(privilegesAdmin)) {
            user.roles.remove(privilegesAdmin);
        }
    }

    public boolean isAdmin() {
        return user.roles.containsKey(privilegesAdmin);
    }

    public boolean isTeacher() {
        return user.roles.containsKey(privilegesTeacher);
    }

    public boolean isMentor() {
        return user.roles.containsKey(privilegesMentor);
    }

    public boolean isStudent() {
        return user.roles.containsKey(privilegesStudent);
    }

    public String getName() {
        return this.user.name;
    }

    public String getSurname() {
        return this.user.surname;
    }

    public String getAge() {
        return this.user.age;
    }

    public String getPassword() {
        return this.user.password;
    }

    public String getDescription() {
        return this.user.description;
    }

    public String getProfilePicture() {
        return this.user.profilePicture;
    }

    public String getId() {
        return this.user.id;
    }

    public void setProfilePicture(String profilePicture) {
        this.user.profilePicture = profilePicture;
    }

    public void setName(String name) {
        this.user.name = name;
    }

    public void setPassword(String password) {
        this.user.password = password;
    }

    public void setDescription(String description) {
        this.user.description = description;
    }

    public void setAge(String age) {
        this.user.age = age;
    }

    public void setSurname(String surname) {
        this.user.surname = surname;
    }

    public User getUser() {
        return this.user;
    }

    public void setEmail(String email) {
        this.user.email = email;
    }
}
