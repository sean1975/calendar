package com.sean.calendar;

import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.extensions.appengine.datastore.AppEngineDataStoreFactory;
import com.google.api.client.googleapis.util.Utils;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.services.calendar.CalendarScopes;
import com.google.appengine.api.users.UserServiceFactory;

public class AuthUtils {
    public static final String CLIENT_SECRETS_PATH = "/WEB-INF/client_secrets.json";
    public static final String CLIENT_SECRETS_ENCODING = "UTF-8";
    private static final String CALLBACK_URL = "/oauth2callback";
    private static final String AFTER_LOGOUT_URL = "/analytics";
    private static final List<String> SCOPES = Arrays.asList(CalendarScopes.CALENDAR_READONLY);

    private static DataStoreFactory DATA_STORE_FACTORY;
    static {
        try {
            DATA_STORE_FACTORY = AppEngineDataStoreFactory.getDefaultInstance();
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }
    
    private final ServletContext sc;
    protected void log(String message) {
        sc.log(message);
    }

    private static AuthUtils instance = null;

    protected AuthUtils(ServletContext sc) {
        this.sc = sc;
    };

    public static synchronized AuthUtils getInstance(ServletContext sc) {
        if (instance == null) {
            instance = new AuthUtils(sc);
        }
        return instance;
    }

    protected static String getLogoutUri(HttpServletRequest req) {
        return UserServiceFactory.getUserService().createLogoutURL(AFTER_LOGOUT_URL);
    }
    
    protected static String getRedirectUri(HttpServletRequest req) throws ServletException, IOException {
        GenericUrl url = new GenericUrl(req.getRequestURL().toString());
        url.setRawPath(CALLBACK_URL);
        return url.build();
    }

    protected static String getUserId(HttpServletRequest req) throws ServletException, IOException {
        return UserServiceFactory.getUserService().getCurrentUser().getUserId();
    }

    protected AuthorizationCodeFlow initializeFlow(Reader clientSecretsStream) throws IOException {        
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(Utils.getDefaultJsonFactory(),
                clientSecretsStream);
        GoogleAuthorizationCodeFlow.Builder builder = new GoogleAuthorizationCodeFlow.Builder(
                Utils.getDefaultTransport(), Utils.getDefaultJsonFactory(), clientSecrets, SCOPES);
        return builder.setDataStoreFactory(DATA_STORE_FACTORY).setAccessType("offline").setApprovalPrompt("force")
                .build();
    }

}
