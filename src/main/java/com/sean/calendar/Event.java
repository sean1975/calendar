package com.sean.calendar;

import java.util.Date;

import com.google.api.client.util.DateTime;

public class Event implements Comparable<Event> {
    private String summary;
    private Integer occurrence;
    private Date startTime;

    public String getSummary() {
        return this.summary;
    }

    public Integer getOccurrence() {
        return this.occurrence;
    }

    public Date getStartTime() {
        return this.startTime;
    }

    public Event(String summary, Integer occurrence, DateTime startTime) {
        this.summary = summary;
        this.occurrence = occurrence;
        this.startTime = new java.util.Date(startTime.getValue());
    }

    @Override
    public int compareTo(Event o) {
        if (this.occurrence > o.getOccurrence())
            return -1;
        else if (this.occurrence < o.getOccurrence())
            return 1;
        else
            return o.getStartTime().compareTo(this.getStartTime());
    }

}
