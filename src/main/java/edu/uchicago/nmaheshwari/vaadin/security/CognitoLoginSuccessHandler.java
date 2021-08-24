package edu.uchicago.nmaheshwari.vaadin.security;

import edu.uchicago.nmaheshwari.vaadin.cache.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CognitoLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    @Autowired
    private OAuth2AuthorizedClientService clientService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        super.onAuthenticationSuccess(request, response, authentication);

        OAuth2AuthenticationToken oauth2Token = (OAuth2AuthenticationToken) authentication;
        //OAuth2AuthorizedClient client =
        //	clientService.loadAuthorizedClient(oauth2Token.getAuthorizedClientRegistrationId(),
        //oauth2Token.getName());
        DefaultOAuth2User principal = ((DefaultOAuth2User) authentication.getPrincipal());
        Cache.getInstance().setEmail(principal.getAttributes().get("email").toString());
        Cache.getInstance().setKeyword("");
        Cache.getInstance().clearItems();

    }

}