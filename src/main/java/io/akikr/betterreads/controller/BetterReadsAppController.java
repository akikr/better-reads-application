package io.akikr.betterreads.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @apiNote : The main application controller
 * @author ankit
 * @since 1.0
 */

@RestController
public class BetterReadsAppController
{
    /**
     * @apiNote : The user endpoint to get the user name after a User logs in via GitHub-OAuth service
     * @param principal : The GitHub OAuth2User object containing the GitHub user info
     * @return string : The name of user who logs in via GitHub-OAuth service
     */
    @RequestMapping("/user")
    public String user(@AuthenticationPrincipal OAuth2User principal)
    {
        return ("<h1>" + principal.getAttribute("name") +  "</h1>" + "<h2>" + principal.getAttribute("email") + "</h2>");
    }
}
