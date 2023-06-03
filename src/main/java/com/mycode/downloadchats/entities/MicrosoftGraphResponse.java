package com.mycode.downloadchats.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class MicrosoftGraphResponse {

    @JsonProperty("@odata.count")
    private Long messagesCount;
    @JsonProperty("@odata.nextLink")
    private String nextMessagesPage;
    @JsonProperty("value")
    private List<Object> pageMessages;

    public MicrosoftGraphResponse() {
        this.messagesCount = 0L;
        this.nextMessagesPage = "";
        this.pageMessages = new ArrayList<>();
    }

}
