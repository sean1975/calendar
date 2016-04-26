package com.sean.calendar;

import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.appengine.auth.oauth2.AbstractAppEngineAuthorizationCodeServlet;
import com.google.api.client.googleapis.util.Utils;
import com.google.api.client.util.DateTime;

import java.io.IOException;
import java.io.InputStreamReader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AnalyticsServlet extends AbstractAppEngineAuthorizationCodeServlet {
    private static final long serialVersionUID = 1L;
    private static final String APPLICATION_NAME = "Sean's Calendar Analytics";
    private static final String CALENDAR_NAME = "primary";
    private static final String RESULT_PAGE = "/analytics.jsp";
    private static final String DISTRIBUTION_PAGE = "/distribution.jsp";
    private static final String PARA_NAME_SUMMARY = "summary";
    private static final String PARA_NAME_START = "start";
    private static final String PARA_NAME_END = "end";
    private static final String ATTR_NAME_DISTRIBUTION = "distribution";
    private static final String ATTR_NAME_EVENTS = "events";
    private static final String ATTR_NAME_CALENDAR = "calendar";
    private Set<String> summarySet;
    private TimeZone timeZone;
    private Date startDate;
    private Date endDate;

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        getServletContext().log("Handling request: " + req.getRequestURL());

        Credential credential = this.getCredential();
        if (credential == null) {
            getServletContext().log("No credential");
            resp.sendRedirect(this.getRedirectUri(req));
        }
        getServletContext().log("Credential for user [" + AuthUtils.getUserId(req) + "] is expiring in "
                + credential.getExpiresInSeconds() + " seconds");

        Calendar service = new Calendar.Builder(Utils.getDefaultTransport(), Utils.getDefaultJsonFactory(), credential)
                .setApplicationName(APPLICATION_NAME).build();
        com.google.api.services.calendar.model.Calendar calendar = service.calendars().get(CALENDAR_NAME).execute();
        timeZone = TimeZone.getTimeZone(calendar.getTimeZone());
        
        // Get parameters from HTTP request
        getParameterSummary(req);
        getParameterStart(req);
        getParameterEnd(req);

        // Set attribute calendar
        com.sean.calendar.Calendar calendarObject = new com.sean.calendar.Calendar(calendar.getSummary(),
                calendar.getTimeZone(), startDate, endDate);
        req.setAttribute(ATTR_NAME_CALENDAR, calendarObject);
        
        if (summarySet != null && (summarySet.size() > 0)) {
            Distribution distribution = getDistribution(service);
            req.setAttribute(ATTR_NAME_DISTRIBUTION, distribution);

            // Forward to the result page for displaying distribution list
            RequestDispatcher rd = req.getRequestDispatcher(DISTRIBUTION_PAGE);
            rd.forward(req, resp);
        } else {            
            ArrayList<com.sean.calendar.Event> eventList = getEventList(service);
            req.setAttribute(ATTR_NAME_EVENTS, eventList);

            // Forward to the result page for displaying event list
            RequestDispatcher rd = req.getRequestDispatcher(RESULT_PAGE);
            rd.forward(req, resp);
        }
    }

    private Integer getEventMap(Calendar service, Map<String, List<Event>> eventMap, Set<String> summarySet)
            throws IOException {
        // Iterate over the events in the specified calendar
        Integer totalOccurrence = 0;
        String pageToken = null;
        do {
            com.google.api.services.calendar.Calendar.Events.List eventsList = service.events().list(CALENDAR_NAME);
            eventsList.setSingleEvents(true).setOrderBy("startTime").setPageToken(pageToken);
            eventsList.setTimeMin(new DateTime(startDate, timeZone));
            eventsList.setTimeMax(new DateTime(endDate, timeZone));
            Events events = eventsList.execute();
            List<Event> items = events.getItems();
            for (Event event : items) {
                // Use upper case of summary as the key for map container
                String eventSummary = event.getSummary().toUpperCase();
                if (summarySet != null && summarySet.contains(eventSummary) == false) {
                    continue;
                }
                List<Event> eventList = null;
                if (eventMap.containsKey(eventSummary)) {
                    eventList = eventMap.get(eventSummary);
                } else {
                    eventList = new ArrayList<Event>();
                }
                eventList.add(event);
                eventMap.put(eventSummary, eventList);
                totalOccurrence++;
            }
            pageToken = events.getNextPageToken();
        } while (pageToken != null);
        return totalOccurrence;
    }
    
    class EventDateComparator implements Comparator<Event> {

        @Override
        public int compare(Event e1, Event e2) {
            DateTime t1 = e1.getStart().getDateTime();
            if (t1 == null)
                t1 = e1.getStart().getDate();
            DateTime t2 = e2.getStart().getDateTime();
            if (t2 == null)
                t2 = e2.getStart().getDate();
            if (t1.getValue() < t2.getValue())
                return -1;
            else if (t1.getValue() == t2.getValue())
                return 0;
            return 1;
        }
        
    }
    
    private Distribution getDistribution(Calendar service) throws IOException {
        Map<String, List<Event>> eventMap = new HashMap<String, List<Event>>();
        getEventMap(service, eventMap, summarySet);
        // Put empty event list if the specific event is not found from startDate to endDate
        for (String summary : summarySet) {
            if (! eventMap.containsKey(summary)) {
                eventMap.put(summary, new ArrayList<Event>());
            }
        } 

        // Iterate all events to compute the occurrence distribution
        Distribution distribution = new Distribution(startDate, endDate, timeZone);
        List<Date> period = distribution.getPeriod();
        for (Map.Entry<String, List<Event>> eventEntry : eventMap.entrySet()) {
            List<Event> events = eventEntry.getValue();
            Collections.sort(events, new EventDateComparator());
            String event = eventEntry.getKey();
            if (events.size() > 0) {
                // The key of map container is upper case, so use the last event
                // summary instead
                Event lastEvent = events.get(events.size() - 1);
                event = lastEvent.getSummary();
            }
            List<Integer> occurrences = new ArrayList<Integer>(period.size()-1);
            for (int index=0; index<period.size()-1; index++) {
                occurrences.add(0);
            }
            // Events between left and right occur in the interval defined by breakpoint period.get(i-1) and period.get(i)
            int i=1, right=0, left=right;
            for (; i<period.size(); i++) {
                for (; right<events.size(); right++) {
                    DateTime startTime = events.get(right).getStart().getDateTime();
                    if (startTime == null) {
                        startTime = events.get(right).getStart().getDate();
                    }
                    if (startTime.getValue() >= period.get(i).getTime()) {
                        occurrences.set(i-1, right-left);
                        left = right;
                        break;
                    }
                }
                // Add the remaining events
                if (right != left) {
                    occurrences.set(i-1, right-left);
                    break;
                }
            }
            distribution.addDistribution(event,  occurrences);
        }
        return distribution;
    }

    private ArrayList<com.sean.calendar.Event> getEventList(Calendar service)
            throws IOException {
        Map<String, List<Event>> eventMap = new HashMap<String, List<Event>>();
        Integer totalOccurrence = getEventMap(service, eventMap, null);

        // Sort events by occurrences
        ArrayList<com.sean.calendar.Event> eventList = new ArrayList<com.sean.calendar.Event>();
        for (Map.Entry<String, List<Event>> eventEntry : eventMap.entrySet()) {
            Integer occurrence = eventEntry.getValue().size();
            Float occurrencePercentage = occurrence / new Float(totalOccurrence);
            // The key of map container is upper case, so use the last event
            // summary instead
            Event lastEvent = eventEntry.getValue().get(occurrence - 1);
            String summary = lastEvent.getSummary();
            DateTime startTime = lastEvent.getStart().getDateTime();
            // getDateTime() may be null for all-day events
            if (startTime == null) {
                startTime = lastEvent.getStart().getDate();
            }
            com.sean.calendar.Event event = new com.sean.calendar.Event(summary, occurrence, occurrencePercentage,
                    startTime);
            eventList.add(event);
        }
        Collections.sort(eventList);
        return eventList;
    }

    private void getParameterEnd(HttpServletRequest req) {
        String end = req.getParameter(PARA_NAME_END);
        try {
            endDate = new SimpleDateFormat("yyyy-MM-dd").parse(end);
        } catch (ParseException e) {
            getServletContext().log("Parameter " + PARA_NAME_END + "[" + end + "] is invalid.");
        } catch (Exception e) {
            // ignore
        }
        if (endDate == null) {
            endDate = new Date();
        }
        java.util.Calendar cal = java.util.Calendar.getInstance(timeZone);
        cal.setTime(endDate);
        cal.set(java.util.Calendar.HOUR_OF_DAY, 23);
        cal.set(java.util.Calendar.MINUTE, 59);
        cal.set(java.util.Calendar.SECOND, 59);
        endDate = cal.getTime();
    }

    private void getParameterStart(HttpServletRequest req) {
        String start = req.getParameter(PARA_NAME_START);
        try {
            startDate = new SimpleDateFormat("yyyy-MM-dd").parse(start);
            java.util.Calendar cal = java.util.Calendar.getInstance(timeZone);
            cal.setTime(startDate);
            cal.set(java.util.Calendar.HOUR_OF_DAY, 0);
            cal.set(java.util.Calendar.MINUTE, 0);
            cal.set(java.util.Calendar.SECOND, 0);
            startDate = cal.getTime();
        } catch (ParseException pe) {
            getServletContext().log("Parameter " + PARA_NAME_START + "[" + start + "] is invalid.");
        } catch (Exception e) {
            // ignore
        }
        if (startDate == null) {
            java.util.Calendar cal = java.util.Calendar.getInstance(timeZone);
            cal.add(java.util.Calendar.MONTH, -6);
            cal.set(java.util.Calendar.HOUR_OF_DAY, 0);
            cal.set(java.util.Calendar.MINUTE, 0);
            cal.set(java.util.Calendar.SECOND, 0);
            startDate = cal.getTime();
        }
    }

    private void getParameterSummary(HttpServletRequest req) {
        String summary = req.getParameter(PARA_NAME_SUMMARY);
        if (summary == null) {
            summarySet = null;
            return;
        }
        getServletContext().log("Parameter [" + PARA_NAME_SUMMARY + "]: " + summary);
        String summaryArray[] = summary.split(",");
        if (summaryArray.length > 0) {
            summarySet = new HashSet<String>();
            for (int i = 0; i < summaryArray.length; i++) {
                summarySet.add(summaryArray[i].toUpperCase());
            }
        }
    }

    @Override
    protected String getRedirectUri(HttpServletRequest req) throws ServletException, IOException {
        return AuthUtils.getRedirectUri(req);
    }

    @Override
    protected AuthorizationCodeFlow initializeFlow() throws ServletException, IOException {
        InputStreamReader clientSecretsStream = new InputStreamReader(
                this.getServletContext().getResourceAsStream(AuthUtils.CLIENT_SECRETS_PATH),
                AuthUtils.CLIENT_SECRETS_ENCODING);
        return AuthUtils.getInstance(getServletContext()).initializeFlow(clientSecretsStream);
    }
}
