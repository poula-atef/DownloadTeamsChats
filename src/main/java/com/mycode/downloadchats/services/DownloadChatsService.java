package com.mycode.downloadchats.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycode.downloadchats.entities.MicrosoftGraphResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.util.Base64;
import java.util.List;

@Service
public class DownloadChatsService {

    @Autowired
    private MicrosoftGraphService graphService;

    private ObjectMapper mapper = new ObjectMapper();


    public Long downloadAllChats(String token) {

        String userName = getUserName(token);

        MicrosoftGraphResponse users = graphService.getAllUsers(token);

        for (int i = 0; i < users.getMessagesCount(); i++) {

            try {

                String chatStr = mapper.writeValueAsString(users.getPageMessages().get(i));
                JSONObject chat = new JSONObject(chatStr);
                MicrosoftGraphResponse chatMessages = graphService.getMessagesForUser(chat.optString("id", ""), token);

                String fileName = "";

                if (chat.optString("chatType") == null) {
                    System.out.println("There is no chatType for this chat");
                    continue;
                }

                if (chat.optJSONArray("members") == null) {
                    System.out.println("There is no members for this chat");
                    continue;
                }


                if (chat.optString("chatType").equals("oneOnOne")) {
                    fileName = "Chat-" + userName + " && ";

                    if (chat.optJSONArray("members").length() < 2) {
                        System.out.println("You are the only member in this chat");
                        continue;
                    }

                    JSONObject firstUser = chat.optJSONArray("members").optJSONObject(0);
                    JSONObject secondUser = chat.optJSONArray("members").optJSONObject(1);

                    if (firstUser.optString("displayName") == null || secondUser.optString("displayName") == null) {
                        System.out.println("There is one of the two members doesn't have a username");
                        continue;
                    }
                    fileName += (firstUser.optString("displayName").equals(userName) ?
                            secondUser.optString("displayName") :
                            firstUser.optString("displayName"));
                } else if (chat.has("topic") && !chat.optString("topic", "").isEmpty()) {
                    fileName = "Group-" + chat.optString("topic");
                } else {
                    JSONArray members = chat.optJSONArray("members");
                    fileName += "Group-";
                    StringBuilder builder = new StringBuilder();
                    for (int j = 0; j < members.length(); j++) {
                        builder.append(members.optJSONObject(0).optString("displayName"));
                        if (j < members.length() - 1)
                            builder.append(" && ");
                    }
                    fileName += builder.toString();
                }


                if (!fileName.isEmpty())
                    writeMessagesToJSONFile(fileName, chatMessages.getPageMessages());
                else
                    System.out.println("Chat Hasn't Been Saved");


            } catch (Exception e) {
                System.out.println("Error Happened While Requesting Users Messages, Error Message :: " + e.getMessage());
            }

        }

        return users.getMessagesCount();
    }

    private void writeMessagesToJSONFile(String fileName, List<Object> pageMessages) {
        String desktopPath = System.getProperty("user.home") + File.separator + "Desktop/Teams Chats";

        try {

            JSONArray obj = new JSONArray();

            for (int i = 0; i < pageMessages.size(); i++) {
                String messageStr = mapper.writeValueAsString(pageMessages.get(i));
                JSONObject message = new JSONObject(messageStr);

                if (
                        message.isEmpty() ||
                                message.optJSONObject("from") == null ||
                                message.optJSONObject("from").optJSONObject("user") == null ||
                                message.optJSONObject("from").optJSONObject("user").optString("displayName") == null ||
                                message.optJSONObject("body") == null ||
                                message.optJSONObject("body").optString("content") == null
                )
                    continue;

                String sender = message.optJSONObject("from").optJSONObject("user").optString("displayName");

                String fileMessage = sender + " :: " + message.optJSONObject("body").optString("content");

                obj.put(fileMessage);
            }

            // create folder if it doesn't exist
            new File(desktopPath).mkdirs();
            FileWriter file = new FileWriter(desktopPath + File.separator + fileName + ".json");

            file.write(obj.toString());
            file.close();

            System.out.println("Chat With Name " + fileName + " Has Been Exported");

        } catch (Exception e) {
            System.out.println("Error Happened While Saving Chat Messages To File, Error Message :: " + e.getMessage());
        }
    }

    private String getUserName(String token) {
        // any token is formed from (headers, payload, signature) respectively
        String[] tokenParts = token.split("\\.");
        String tokenPayload = tokenParts[1];

        Base64.Decoder decoder = Base64.getUrlDecoder();

        String decodedTokenPayload = new String(decoder.decode(tokenPayload));

        JSONObject payload = new JSONObject(decodedTokenPayload);

        return payload.optString("name", "");
    }

}
