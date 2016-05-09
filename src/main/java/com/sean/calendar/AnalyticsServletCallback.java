package com.sean.calendar;

import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.AuthorizationCodeResponseUrl;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.servlet.auth.oauth2.AbstractAuthorizationCodeCallbackServlet;

public class AnalyticsServletCallback extends AbstractAuthorizationCodeCallbackServlet {
    private static final long serialVersionUID = 1L;
    private static final String LANDING_PAGE = "/analytics";

    @Override
    protected String getRedirectUri(HttpServletRequest req) throws ServletException, IOException {
        return AuthUtils.getRedirectUri(req);
    }

    @Override
    protected String getUserId(HttpServletRequest req) throws ServletException, IOException {
        return AuthUtils.getUserId();
    }

    @Override
    protected AuthorizationCodeFlow initializeFlow() throws ServletException, IOException {
        InputStreamReader clientSecretsStream = new InputStreamReader(
                getServletContext().getResourceAsStream(AuthUtils.CLIENT_SECRETS_PATH),
                AuthUtils.CLIENT_SECRETS_ENCODING);
        return AuthUtils.getInstance(getServletContext()).initializeFlow(clientSecretsStream);
    }

    @Override
    protected void onSuccess(HttpServletRequest req, HttpServletResponse resp, Credential credential)
            throws ServletException, IOException {
        getServletContext().log("Authenticated by " + credential.getTokenServerEncodedUrl()
                + ", credential will expire in " + credential.getExpiresInSeconds() + " seconds");
        resp.sendRedirect(LANDING_PAGE);
    }

    @Override
    protected void onError(HttpServletRequest req, HttpServletResponse resp, AuthorizationCodeResponseUrl errorResponse)
            throws ServletException, IOException {
        getServletContext().log("Authentication failed: " + errorResponse.getError());
    }
}
