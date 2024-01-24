package com.example.mybud.mybud;

import com.google.firebase.database.DataSnapshot;

public class expense {
    String key;
    String date;
    String type;
    Float amount;
    String description;
    Boolean deduct;

    public expense()
    {

    }

    public expense(String key,String d, String str_type, Float fl_amt, String str_desc, boolean checked){
        this.key=key;
        this.date=d;
        this.type=str_type;
        this.amount=fl_amt;
        this.description=str_desc;
        this.deduct=checked;
    }

    //GETTER METHODS

    public String getKey(){return key;}

    public String getDate() {
        return date;
    }

    public String getType() { return type; }

    public String getDescription() {
        return description;
    }

    public Float getAmount() {
        return amount;
    }

    public Boolean getDeduct() {
        return deduct;
    }


    //SETTER METHODS

    public void setKey(String key){this.key=key;}

    public void setDate(String date) {
        this.date = date;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public void setDeduct(Boolean deduct) {
        this.deduct = deduct;
    }
}
