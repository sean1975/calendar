package com.sean.calendar;

import java.util.Date;

public class Calendar {
    private String summary;
    private String timeZone;
    private Date start;
    private Date end;
    
    public Calendar(String summary, String timeZone, Date start, Date end) {
        this.summary = summary;
        this.timeZone = timeZone;
        this.start = start;
        this.end = end;
    }

    public String getSummary() {
        return this.summary;
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
