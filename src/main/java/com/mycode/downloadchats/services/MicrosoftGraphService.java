package com.mycode.downloadchats.services;

import com.mycode.downloadchats.entities.MicrosoftGraphResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;

@Service
public class MicrosoftGraphService {

    @Autowired
    private RestTemplate template;

    public MicrosoftGraphResponse getMessagesForUser(String userId, String token) {

        String url = "https://graph.microsoft.com/beta/me/chats/" + userId + "/messages?$top=50";

        return performMicrosoftGraphOperation(token, url, "Messages");
    }

    public MicrosoftGraphResponse getAllUsers(String token) {
        String url = "https://graph.microsoft.com/beta/me/chats?$expand=members&$top=50";

        return performMicrosoftGraphOperation(token, url, "Users");
    }


    private MicrosoftGraphResponse performMicrosoftGraphOperation(String token, String url, String type) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", token);

        HttpEntity<?> entity = new HttpEntity<>(headers);

        MicrosoftGraphResponse microsoftGraphResponse = new MicrosoftGraphResponse(0L, url, new ArrayList<>());

        try {

            while (microsoftGraphResponse.getNextMessagesPage() != null && !microsoftGraphResponse.getNextMessagesPage().isEmpty()) {

                ResponseEntity<MicrosoftGraphResponse> response = template.exchange(
                        microsoftGraphResponse.getNextMessagesPage(),
                        HttpMethod.GET,
                        entity,
                        MicrosoftGraphResponse.class,
                        new HashMap<>()
                );

                if (response == null || response.getBody() == null)
                    break;


                microsoftGraphResponse.setMessagesCount(microsoftGraphResponse.getMessagesCount() + response.getBody().getMessagesCount());
                microsoftGraphResponse.getPageMessages().addAll(response.getBody().getPageMessages());
                microsoftGraphResponse.setNextMessagesPage(response.getBody().getNextMessagesPage());

                System.out.println(type + " Retrieved Yet :: " + microsoftGraphResponse.getMessagesCount());

            }

            System.out.println(microsoftGraphResponse.getMessagesCount() + " messages are retrieved successfully");

        } catch (Exception e) {
            System.out.println("Error Happened While Retrieving " + type + " :: " + e.getMessage());
        }

        return microsoftGraphResponse;
    }

}
