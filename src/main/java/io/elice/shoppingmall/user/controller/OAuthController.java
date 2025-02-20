package io.elice.shoppingmall.user.controller;

import jakarta.servlet.http.HttpServletRequest;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.util.UriComponentsBuilder;



@Controller
@RequiredArgsConstructor
public class OAuthController {


    private final OAuth2AuthorizedClientService authorizedClientService;

    @GetMapping("/api/v1/auth/oauth2/naver")
    public ResponseEntity<String> handleOAuth2AuthorizationRequest(HttpServletRequest request) {
        String authorizationUrl = createNaverAuthorizationUrl(request);
        return ResponseEntity.ok(authorizationUrl);
    }


    private String createNaverAuthorizationUrl(HttpServletRequest request) {
        OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient("naver", request.getSession().getId());

        if (authorizedClient == null) {
            return "redirect:/oauth2/authorization/naver";
        }

        String authorizationUrl = authorizedClient.getClientRegistration().getProviderDetails().getAuthorizationUri();
        String state = UUID.randomUUID().toString();

        return UriComponentsBuilder.fromUriString(authorizationUrl)
            .queryParam("response_type", "code")
            .queryParam("client_id", authorizedClient.getClientRegistration().getClientId())
            .queryParam("redirect_uri", authorizedClient.getClientRegistration().getRedirectUri())
            .queryParam("state", state)
            .build()
            .toUriString();
    }
}



