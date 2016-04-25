package com.sean.calendar;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class Distribution {
    private final List<Date> period;
    private final TimeZone timeZone;
    private Map<String, List<Integer>> distributionMap;
    
    private static final long MILLISECONDS_IN_A_DAY = 1000 * 60 * 60 * 24;
    private static final long EIGHT_WEEKS = 8 * 7;
    private static final long THREE_WEEKS = 3 * 7;

    public List<Date> getPeriod() {
        return period;
    }
    
    // Get formatted interval strings. Maybe this should be done in distribution.jsp by JSP/JSTL
    public List<String> getIntervals() {
        ArrayList<String> intervals = new ArrayList<String>();
        for (int i=0; i<period.size()-1; i++) {
            long duration = (period.get(i+1).getTime() - period.get(i).getTime());
            long duration_in_day = duration / MILLISECONDS_IN_A_DAY;
            java.util.Calendar cal = java.util.Calendar.getInstance(timeZone);
            cal.setTime(period.get(i));
            String interval;
            DateFormat df;
            if (duration_in_day > THREE_WEEKS) {
                df = new SimpleDateFormat("MMM");
                interval = df.format(period.get(i));
            } else if (duration_in_day > 1) {
                df = new SimpleDateFormat("MMM dd");
                interval = df.format(period.get(i));
                interval += " ~ ";
                interval += df.format(period.get(i+1));
            } else {
                df = new SimpleDateFormat("MMM dd");
                interval = df.format(period.get(i));
            }
            intervals.add(interval);
        }
        return intervals;
    }
    
    public List<String> getEventList() {
        ArrayList<String> eventList = new ArrayList<String>();
        eventList.addAll(distributionMap.keySet());
        return eventList;
    }
    
    public Map<String, List<Integer>> getDistributionMap() {
        return distributionMap;
    }
    
    public boolean addDistribution(String event, List<Integer> occurrences) {
        if (occurrences.size() != period.size() - 1)
            return false;
        distributionMap.put(event,  occurrences);
        return true;
    }
    
    public Distribution(Date start, Date end, TimeZone timeZone) {
        this.timeZone = timeZone;
        // Break the period between start and end into intervals
        // Intervals are defined by breakpoint Date,
        // so the number of breakpoints is more than the number of intervals by 1
        // Ex: Breakpoint 1 (startDate) <- Interval 1 -> Breakpoint 2 <- Interval 2 ... Interval N -> Breakpoint N+1 (endDate)
        period = new ArrayList<Date>();
        java.util.Calendar startCal = java.util.Calendar.getInstance(this.timeZone);
        startCal.setTime(start);
        java.util.Calendar endCal = java.util.Calendar.getInstance(this.timeZone);
        endCal.setTime(end);
        long duration = endCal.getTimeInMillis() - startCal.getTimeInMillis();
        long duration_in_day = duration / MILLISECONDS_IN_A_DAY;
        while (endCal.after(startCal)) {
            period.add(startCal.getTime());
            if (duration_in_day > EIGHT_WEEKS) {
                // For duration longer than 8 weeks, align the intervals with calendar months
                startCal.add(java.util.Calendar.MONTH, +1);
                startCal.set(java.util.Calendar.DAY_OF_MONTH, 1);
            } else if (duration_in_day > THREE_WEEKS) {
                startCal.add(java.util.Calendar.DATE, +7);
            } else {
                startCal.add(java.util.Calendar.DATE, +1);
            }
        }
        // Set the last breakpoint (endDate)
        period.add(endCal.getTime());
        
        distributionMap = new HashMap<String, List<Integer>>();
    }
}
