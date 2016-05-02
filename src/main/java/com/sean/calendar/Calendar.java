package com.sean.calendar;

import java.util.Date;

public class Calendar {
    private String summary;
    private String id;
    private String timeZone;
    private Date start;
    private Date end;
    
    public Calendar(com.google.api.services.calendar.model.Calendar calendar, Date start, Date end) {
        this.summary = calendar.getSummary();
        this.id = calendar.getId();
        this.timeZone = calendar.getTimeZone();
        this.start = start;
        this.end = end;
    }

    public String getSummary() {
        return this.summary;
    }
    
    public String getId() {
        return this.id;
    }
    
    public String getTimeZone() {
        return this.timeZone;
    }
    
    public Date getStart() {
        return this.start;
    }
    
    public Date getEnd() {
        return this.end;
    }
}
