package com.example.ramesh.internshala.Data;

public class User {
    public String userid;
    public String city;
    public String mobile;
    public String url;
    public String password;
    public String email;
    public String name;
   public String Contact1;
    public String Contact2;
    public String Contact3;
    public String Contact4;
    public String Contact5;
    public String usertype;


    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String name, String userid, String city,String mobile,String url,String password,String Contact1,String Contact2,String Contact3,String Contact4,String Contact5,String usertype,String email) {
        this.name=name;
        this.userid = userid;
        this.city=city;
        this.mobile=mobile;
        this.url=url;
        this.password=password;
        this.Contact1=Contact1;
        this.Contact2=Contact2;
        this.Contact3=Contact3;
        this.Contact4=Contact4;
        this.Contact5=Contact5;
        this.usertype=usertype;
        this.email=email;
    }

}
