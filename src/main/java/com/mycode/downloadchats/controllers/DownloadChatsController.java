package com.mycode.downloadchats.controllers;

import com.mycode.downloadchats.services.DownloadChatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/downloadTeamsChats")
public class DownloadChatsController {

    @Autowired
    private DownloadChatsService service;

    @GetMapping({"/", ""})
    public String getUserMessages(@RequestHeader("Authorization") String token) {
        Long result = service.downloadAllChats(token);
        return "There Are " + result + " Chats Have Been Downloaded Successfully";
    }


}
