package com.sean.calendar;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.TimeZone;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;

public class AnalyticsServletTest {
    
    protected AnalyticsServlet servlet;
    protected ServletContext servletContext;
    protected ServletConfig servletConfig;
    protected PrintStream logger = System.out;
    protected static final String PARA_NAME_START = "start"; // AnalyticsServlet.PARA_NAME_START
    protected static final String PARA_NAME_END = "end"; // AnalyticsServlet.PARA_NAME_END
    protected static final String PARA_NAME_EXCLUDE = "exclude"; // AnalyticsServlet.PARA_NAME_EXCLUDE
    protected Method getParameterStart; // AnalyticsServlet.getParameterStart()
    protected Method getParameterEnd; // AnalyticsServlet.getParameterEnd()
    protected Method getParameterExclude; // AnalyticsServlet.getParameterExclude()
    
    @SuppressWarnings("rawtypes")
    @Before
    public void setUp() throws ServletException {
        servletContext = spy(ServletContext.class);
        doNothing().when(servletContext).log(null);
        servletConfig = mock(ServletConfig.class);
        when(servletConfig.getInitParameter("defaultPool")).thenReturn("testpool1");
        when(servletConfig.getServletContext()).thenReturn(servletContext);
        servlet = new AnalyticsServlet();
        servlet.init(servletConfig);
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
            // AnalyticsServlet.getParameterExclude()
            getParameterExclude = servlet.getClass().getDeclaredMethod("getParameterExclude", cArg);
            getParameterExclude.setAccessible(true);
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
    
    @SuppressWarnings("unchecked")
    @Test
    public void testGetParameterExclude() {
        String excludeString = "open,close";
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getParameter(PARA_NAME_EXCLUDE)).thenReturn(excludeString);
        try {
            getParameterExclude.invoke(servlet, req);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
        verify(req).getParameter(PARA_NAME_EXCLUDE);
        Set<String> excludeSet = (Set<String>) getField("excludeSet");
        assertNotNull(excludeSet);
        assertTrue(excludeSet.size() == 2);
        assertTrue(excludeSet.contains("OPEN"));
        assertFalse(excludeSet.contains("open"));
        assertTrue(excludeSet.contains("CLOSE"));
        assertFalse(excludeSet.contains("close"));
    }

}
