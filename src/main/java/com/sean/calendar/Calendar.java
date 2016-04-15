package com.sean.calendar;

public class Calendar {
    private String summary;
    private String timeZone;
    
    public Calendar(String summary, String timeZone) {
        this.summary = summary;
        this.timeZone = timeZone;
    }

    public String getSummary() {
        return this.summary;
    }
    
    public String getTimeZone() {
        return this.timeZone;
    }
}
