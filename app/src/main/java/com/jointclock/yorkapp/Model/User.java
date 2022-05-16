package com.jointclock.yorkapp.Model;

public class User {
    private String userid;
    private String useremail;
    private String userfirstname;
    private String userlastname;
    private String userphone;
    private String userpassword;
    private String userphotoid;
    private String usertype;

    public User(String userid, String useremail, String userfirstname, String userlastname, String userphone, String userpassword,String userphotoid,String usertype) {
        this.userid = userid;
        this.useremail = useremail;
        this.userfirstname = userfirstname;
        this.userlastname = userlastname;
        this.userphone = userphone;
        this.userpassword = userpassword;
        this.userphotoid = userphotoid;
        this.usertype = usertype;
    }

    public User() {
    }

    public String getuserid() {
        return userid;
    }

    public void setuserid(String userid) {
        this.userid = userid;
    }

    public String getuseremail() {
        return useremail;
    }

    public void setuseremail(String useremail) {
        this.useremail = useremail;
    }

    public String getuserfirstname() {
        return userfirstname;
    }

    public void setuserfirstname(String userfirstname) {
        this.userfirstname = userfirstname;
    }

    public String getuserlastname() {
        return userlastname;
    }

    public void setuserlastname(String userlastname) {
        this.userlastname = userlastname;
    }

    public String getuserphone() {
        return userphone;
    }

    public void setuserphone(String userphone) {
        this.userphone = userphone;
    }

    public String getuserpassword() {
        return userpassword;
    }

    public void setuserpassword(String userpassword) {
        this.userpassword = userpassword;
    }
    
    public String getuserphotoid() {
        return userphotoid;
    }

    public void setuserphotoid(String userphotoid) {
        this.userphotoid = userphotoid;
    }

    public String getusertype() {
        return usertype;
    }

    public void setusertype(String usertype) {
        this.usertype = usertype;
    }

    
}
