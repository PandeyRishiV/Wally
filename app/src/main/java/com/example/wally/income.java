package com.example.wally;

public class income {
    String key;
    String date;
    String source;
    Float amount;
    String desc;

    public  income()
    {

    }

    public income(String key,String child_date, String str_source, Float fl_am, String str_desc)
    {
        this.key=key;
        this.date=child_date;
        this.source=str_source;
        this.amount=fl_am;
        this.desc=str_desc;
    }

    //GETTER METHODS


    public String getKey() { return key; }

    public String getDate() {
        return date;
    }

    public String getSource() {
        return source;
    }

    public Float getAmount() {
        return amount;
    }

    public String getDesc() {
        return desc;
    }

    //SETTER METHODS


    public void setKey(String key) { this.key = key; }

    public void setDate(String date) {
        this.date = date;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
