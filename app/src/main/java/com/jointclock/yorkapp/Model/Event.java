package com.jointclock.yorkapp.Model;

public class Event {
    private String eventid;
    private String eventdatetimestart;
    private String eventdatetimeend;
    private String eventname;
    private String eventdescription ;
    private String eventlocation;
    private String eventcontactemail;
    private String eventcontactphone;
    private String eventphotoid;
    private String eventtype;
    private String eventaddcv;
    private String eventhosterid;
    private String eventpaymentvalue;

    public Event(String eventid, String eventdatetimestart,String eventdatetimeend, String eventname, String eventdescription ,String eventlocation,String eventcontactemail, String eventcontactphone,String eventphotoid, String eventtype,String eventaddcv,String eventhosterid,String eventpaymentvalue) {
        this.eventid                 = eventid;
        this.eventdatetimestart      = eventdatetimestart;
        this.eventdatetimeend        = eventdatetimeend;
        this.eventname               = eventname;
        this.eventdescription        = eventdescription;
        this.eventlocation           = eventlocation;
        this.eventcontactemail       = eventcontactemail;
        this.eventcontactphone       = eventcontactphone;
        this.eventphotoid            = eventphotoid;
        this.eventtype               = eventtype;
        this.eventaddcv              = eventaddcv;
        this.eventhosterid           = eventhosterid;
        this.eventpaymentvalue       = eventpaymentvalue;
    }

    public Event() {

    }
    public String geteventid() {
        return eventid;
    }

    public void seteventid(String eventid) {
        this.eventid = eventid;
    }

    public String geteventdatetimestart() {
        return eventdatetimestart;
    }

    public void seteventdatetimestart(String eventdatetimestart) {
        this.eventdatetimestart = eventdatetimestart;
    }
    public String geteventdatetimeend() {
        return eventdatetimeend;
    }

    public void seteventdatetimeend(String eventdatetimeend) {
        this.eventdatetimeend = eventdatetimeend;
    }

    public String geteventname() {
        return eventname;
    }

    public void seteventname(String eventname) {
        this.eventname = eventname;
    }

    public String geteventdescription () {
        return eventdescription ;
    }

    public void seteventdescription (String eventdescription ) {
        this.eventdescription  = eventdescription ;
    }

    public String geteventlocation() {
        return eventlocation;
    }

    public void seteventlocation(String eventlocation) {
        this.eventlocation = eventlocation;
    }

    public String geteventcontactemail() {
        return eventcontactemail;
    }

    public void seteventcontactemail(String eventcontactemail) {
        this.eventcontactemail = eventcontactemail;
    }

    public String getEventcontactphone() {
        return eventcontactphone;
    }

    public void setEventcontactphone(String eventcontactphone) {
        this.eventcontactphone = eventcontactphone;
    }

    public String geteventphotoid() {
        return eventphotoid;
    }

    public void seteventphotoid(String eventphotoid) {
        this.eventphotoid = eventphotoid;
    }

    public String geteventtype() {
        return eventtype;
    }

    public void seteventtype(String eventtype) {
        this.eventtype = eventtype;
    }

    public String geteventaddcv() {
        return eventaddcv;
    }

    public void seteventaddcv(String eventaddcv) {
        this.eventaddcv = eventaddcv;
    }

    public String geteventhosterid() {
        return eventhosterid;
    }

    public void seteventhosterid(String eventhosterid) {
        this.eventhosterid = eventhosterid;
    }

    public String geteventpaymentvalue() {
        return eventpaymentvalue;
    }

    public void seteventpaymentvalue(String eventpaymentvalue) {
        this.eventpaymentvalue = eventpaymentvalue;
    }


}
