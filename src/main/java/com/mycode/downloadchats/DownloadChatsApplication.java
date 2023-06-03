package com.mycode.downloadchats;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.FileWriter;

@SpringBootApplication
public class DownloadChatsApplication {

    public static void main(String[] args) {
        SpringApplication.run(DownloadChatsApplication.class, args);
    }

}
