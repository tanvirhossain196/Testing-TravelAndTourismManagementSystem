package model;

import util.DateUtil;

public abstract class user {
    protected String id;
    protected String name;
    protected String email;
    protected String password;
    protected String phone;
    protected String createdDate;
    protected boolean isActive;

    public user(String id, String name, String email, String password, String phone) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.createdDate = DateUtil.getCurrentDate();
        this.isActive = true;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getCreatedDate() { return createdDate; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public abstract String getRole();

    @Override
    public String toString() {
        return "User{id='" + id + "', name='" + name + "', email='" + email + "', phone='" + phone + "', active=" + isActive + "}";
    }
}
