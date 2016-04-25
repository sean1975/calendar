package com.sean.calendar;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;

public class AnalyticsServletTest {
    
    protected AnalyticsServlet servlet;
    protected static final String PARA_NAME_START = "start"; // AnalyticsServlet.PARA_NAME_START
    protected static final String PARA_NAME_END = "end"; // AnalyticsServlet.PARA_NAME_END
    protected Method getParameterStart; // AnalyticsServlet.getParameterStart()
    protected Method getParameterEnd; // AnalyticsServlet.getParameterEnd()
    
    @SuppressWarnings("rawtypes")
    @Before
    public void setUp() {
        servlet = new AnalyticsServlet();
        try {
            Field timeZone = servlet.getClass().getDeclaredField("timeZone");
            timeZone.setAccessible(true);
            timeZone.set(servlet, TimeZone.getDefault());
            Class[] cArg = new Class[1];
            cArg[0] = HttpServletRequest.class;
            // AnalyticsServlet.getParameterStart()
            getParameterStart = servlet.getClass().getDeclaredMethod("getParameterStart", cArg);
            getParameterStart.setAccessible(true);
            // AnalyticsServlet.getParameterEnd()
            getParameterEnd = servlet.getClass().getDeclaredMethod("getParameterEnd", cArg);
            getParameterEnd.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Failed to set up test: " + e.toString());
        }
    }
    
    protected Object getField(String name) {
        Object result = null;
        try {
            Field field = servlet.getClass().getDeclaredField(name);
            field.setAccessible(true);
            result = field.get(servlet);
        } catch (Exception e) {
            // Ignore
        }
        return result;
    }
    
    @Test
    public void testGetParameterStart() throws ParseException {
        String startDateString = "2015-12-01";
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getParameter(PARA_NAME_START)).thenReturn(startDateString);
        try {
            getParameterStart.invoke(servlet, req);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
        verify(req).getParameter(PARA_NAME_START);
        Date startDate = (Date) getField("startDate");
        assertNotNull(startDate);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date inputDate = sdf.parse(startDateString);
        assertTrue("Failed to get startDate", startDate.equals(inputDate));
    }

    @Test
    public void testGetParameterEnd() throws ParseException {
        String endDateString = "2016-04-25";
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getParameter(PARA_NAME_END)).thenReturn(endDateString);
        try {
            getParameterEnd.invoke(servlet, req);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
        verify(req).getParameter(PARA_NAME_END);
        Date endDate = (Date) getField("endDate");
        assertNotNull(endDate);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date inputDate = sdf.parse(endDateString + " 23:59:59");
        assertTrue("Failed to get endDate", endDate.equals(inputDate));
    }

}
