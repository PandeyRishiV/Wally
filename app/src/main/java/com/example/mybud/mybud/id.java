package com.example.mybud.mybud;

import android.util.Patterns;

public class id {
    String email;
    String password;
    String device;

    public id(){

    }

    public id(String email,String password,String device)
    {
        this.email=email;
        this.password=password;
        this.device=device;
    }

    //GETTER METHODS

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getDevice() {
        return device;
    }

    //SETTER METHODS


    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setDevice(String device) {
        this.device = device;
    }
}
