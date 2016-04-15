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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AnalyticsServlet extends AbstractAppEngineAuthorizationCodeServlet {
    private static final long serialVersionUID = 1L;
    private static final String APPLICATION_NAME = "Sean's Calendar Analytics";
    private static final String CALENDAR_NAME = "primary";
    private static final String RESULT_PAGE = "/analytics.jsp";
    private static final String ATTR_NAME_EVENTS = "events";
    private static final String ATTR_NAME_CALENDAR = "calendar";

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

        // TODO: filter events by start/end time, event summary
        // Iterate over the events in the specified calendar
        Map<String, List<Event>> eventMap = new HashMap<String, List<Event>>();
        String pageToken = null;
        do {
            Events events = service.events().list(CALENDAR_NAME).setSingleEvents(true).setOrderBy("startTime")
                    .setTimeMin(new DateTime("2015-12-01T00:00:00+08:00")).setTimeMax(new DateTime(new Date()))
                    .setPageToken(pageToken).execute();
            List<Event> items = events.getItems();
            for (Event event : items) {
                // Use upper case of summary as the key for map container
                String eventSummary = event.getSummary().toUpperCase();
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
