package com.jointclock.yorkapp.Model;

public class Emergency {
    private String emergencyid;
    private String emergencyemail;
    private String emergencyfirstname;
    private String emergencylastname;
    private String emergency_t_h_name;
    private String emergencyphone;
    private String emergencyphotoid;
    private String emergencytype;

    public Emergency(String emergencyid, String emergencyemail, String emergencyfirstname, String emergencylastname,String emergency_t_h_name, String emergencyphone,String emergencyphotoid,String emergencytype) {
        this.emergencyid             = emergencyid;
        this.emergencyemail          = emergencyemail;
        this.emergencyfirstname      = emergencyfirstname;
        this.emergencylastname       = emergencylastname;
        this.emergency_t_h_name      = emergency_t_h_name;
        this.emergencyphone          = emergencyphone;
        this.emergencyphotoid        = emergencyphotoid;
        this.emergencytype           = emergencytype;
    }

    public Emergency() {
    }

    public String getemergencyid() {
        return emergencyid;
    }

    public void setemergencyid(String emergencyid) {
        this.emergencyid = emergencyid;
    }

    public String getemergencyemail() {
        return emergencyemail;
    }

    public void setemergencyemail(String emergencyemail) {
        this.emergencyemail = emergencyemail;
    }

    public String getemergencyfirstname() {
        return emergencyfirstname;
    }

    public void setemergencyfirstname(String emergencyfirstname) {
        this.emergencyfirstname = emergencyfirstname;
    }

    public String getemergencylastname() {
        return emergencylastname;
    }

    public void setemergencylastname(String emergencylastname) {
        this.emergencylastname = emergencylastname;
    }

    public String getemergency_t_h_name() {
        return emergency_t_h_name;
    }

    public void setemergency_t_h_name(String emergency_t_h_name) {
        this.emergency_t_h_name = emergency_t_h_name;
    }

    public String getemergencyphone() {
        return emergencyphone;
    }

    public void setemergencyphone(String emergencyphone) {
        this.emergencyphone = emergencyphone;
    }

    public String getemergencyphotoid() {
        return emergencyphotoid;
    }

    public void setemergencyphotoid(String emergencyphotoid) {
        this.emergencyphotoid = emergencyphotoid;
    }

    public String getemergencytype() {
        return emergencytype;
    }

    public void setemergencytype(String emergencytype) {
        this.emergencytype = emergencytype;
    }

}
