package com.sean.calendar;

import static org.junit.Assert.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.junit.Test;

public class DistributionTest {

    @Test
    public void testDistribution_Day() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date start = sdf.parse("2016-02-01 00:00:00");
        Date end = sdf.parse("2016-02-09 23:59:59");
        Distribution distribution = new Distribution(start, end, TimeZone.getDefault());
        List<String> intervals = distribution.getIntervals();
        assertNotNull(intervals);
        assertTrue(intervals.size() == 9);
        assertTrue(intervals.get(0).compareTo("Feb 01") == 0);
        assertTrue(intervals.get(intervals.size()-1).compareTo("Feb 09") == 0);
    }

    @Test
    public void testDistribution_Week() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date start = sdf.parse("2016-02-01 00:00:00");
        Date end = sdf.parse("2016-02-28 23:59:59");
        Distribution distribution = new Distribution(start, end, TimeZone.getDefault());
        List<String> intervals = distribution.getIntervals();
        assertNotNull(intervals);
        assertTrue(intervals.size() == 4);
        assertTrue(intervals.get(0).compareTo("Feb 01 ~ Feb 07") == 0);
        assertTrue(intervals.get(intervals.size()-1).compareTo("Feb 22 ~ Feb 28") == 0);
    }

    @Test
    public void testDistribution_WeekWithDay() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date start = sdf.parse("2016-02-01 00:00:00");
        Date end = sdf.parse("2016-02-29 23:59:59");
        Distribution distribution = new Distribution(start, end, TimeZone.getDefault());
        List<String> intervals = distribution.getIntervals();
        assertNotNull(intervals);
        assertTrue(intervals.size() == 5);
        assertTrue(intervals.get(0).compareTo("Feb 01 ~ Feb 07") == 0);
        assertTrue(intervals.get(intervals.size()-1).compareTo("Feb 29") == 0);
    }
    
    @Test
    public void testDistribution_Month() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date start = sdf.parse("2016-02-01 00:00:00");
        Date end = sdf.parse("2016-04-26 23:59:59");
        Distribution distribution = new Distribution(start, end, TimeZone.getDefault());
        List<String> intervals = distribution.getIntervals();
        assertNotNull(intervals);
        assertTrue(intervals.size() == 3);
        assertTrue(intervals.get(0).compareTo("Feb") == 0);
        assertTrue(intervals.get(intervals.size()-1).compareTo("Apr") == 0);
    }

    @Test
    public void testDistribution_TimeZone() throws ParseException {
        // User's calendar time zone is Brisbane
        TimeZone.setDefault(TimeZone.getTimeZone("Australia/Brisbane"));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date start = sdf.parse("2016-02-01 00:00:00");
        Date end = sdf.parse("2016-02-09 23:59:59");
        // Server is hosted in time zone Los Angeles
        TimeZone.setDefault(TimeZone.getTimeZone("America/Los_Angeles"));
        // User's calendar time zone is passed to Distribution constructor 
        Distribution distribution = new Distribution(start, end, TimeZone.getTimeZone("Australia/Brisbane"));
        List<String> intervals = distribution.getIntervals();
        for (String interval : intervals) {
            System.out.println(interval);
        }
        assertNotNull(intervals);
        assertTrue(intervals.size() == 9);
        assertTrue(intervals.get(0).compareTo("Feb 01") == 0);
        assertTrue(intervals.get(intervals.size()-1).compareTo("Feb 09") == 0);
    }

}
