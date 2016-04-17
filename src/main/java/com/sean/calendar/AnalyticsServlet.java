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
    private static final String PARA_NAME_SUMMARY = "summary";
    private static final String PARA_NAME_START = "start";
    private static final String PARA_NAME_END = "end";
    private static final String PARA_VALUE_NOW = "now";
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
        com.sean.calendar.Calendar calendarObject = new com.sean.calendar.Calendar(calendar.getSummary(),
                calendar.getTimeZone());
        req.setAttribute(ATTR_NAME_CALENDAR, calendarObject);
        timeZone = TimeZone.getTimeZone(calendar.getTimeZone());
        
        // Get parameters from HTTP request
        getParameterSummary(req);
        getParameterStart(req);
        getParameterEnd(req);

        // Iterate over the events in the specified calendar
        Map<String, List<Event>> eventMap = new HashMap<String, List<Event>>();
        String pageToken = null;
        do {
            com.google.api.services.calendar.Calendar.Events.List eventsList = service.events().list(CALENDAR_NAME);
            eventsList.setSingleEvents(true).setOrderBy("startTime").setPageToken(pageToken);
            if (startDate != null) {
                eventsList.setTimeMin(new DateTime(startDate, timeZone));
            }
            if (endDate != null) {
                eventsList.setTimeMax(new DateTime(endDate, timeZone));
            }
            Events events = eventsList.execute();
            List<Event> items = events.getItems();
            for (Event event : items) {
                // Use upper case of summary as the key for map container
                String eventSummary = event.getSummary().toUpperCase();
                if (summarySet != null && (!summarySet.contains(eventSummary))) {
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
            }
            pageToken = events.getNextPageToken();
        } while (pageToken != null);

        // Sort events by occurrences
        ArrayList<com.sean.calendar.Event> eventList = new ArrayList<com.sean.calendar.Event>();
        for (Map.Entry<String, List<Event>> eventEntry : eventMap.entrySet()) {
            Integer occurrence = eventEntry.getValue().size();
            // The key of map container is upper case, so use the last event
            // summary instead
            Event lastEvent = eventEntry.getValue().get(occurrence - 1);
            String summary = lastEvent.getSummary();
            DateTime startTime = lastEvent.getStart().getDateTime();
            // getDateTime() may be null for all-day events
            if (startTime == null) {
                startTime = lastEvent.getStart().getDate();
            }
            com.sean.calendar.Event event = new com.sean.calendar.Event(summary, occurrence, startTime);
            eventList.add(event);
        }
        Collections.sort(eventList);
        req.setAttribute(ATTR_NAME_EVENTS, eventList);

        // Forward to the result page for displaying event list
        RequestDispatcher rd = req.getRequestDispatcher(RESULT_PAGE);
        rd.forward(req, resp);
    }

    private void getParameterEnd(HttpServletRequest req) {
        String end = req.getParameter(PARA_NAME_END);
        if (end == null) {
            return;
        }
        if (end.compareTo(PARA_VALUE_NOW) == 0) {
            endDate = new Date();
        } else {
            try {
                endDate = new SimpleDateFormat("yyyy-MM-dd").parse(end);
            } catch (ParseException e) {
                getServletContext().log("Parameter " + PARA_NAME_END + "[" + end + "] is invalid.");
            }
        }
    }

    private void getParameterStart(HttpServletRequest req) {
        String start = req.getParameter(PARA_NAME_START);
        if (start == null) {
            return;
        }
        if (start.compareTo(PARA_VALUE_NOW) == 0) {
            startDate = new Date();
        } else {
            try {
                startDate = new SimpleDateFormat("yyyy-MM-dd").parse(start);
            } catch (ParseException e) {
                getServletContext().log("Parameter " + PARA_NAME_START + "[" + start + "] is invalid.");
            }
        }
    }

    private void getParameterSummary(HttpServletRequest req) {
        String summary = req.getParameter(PARA_NAME_SUMMARY);
        if (summary == null) {
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
