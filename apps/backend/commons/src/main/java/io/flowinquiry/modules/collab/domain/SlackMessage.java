package io.flowinquiry.modules.collab.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SlackMessage {

    private String message;
    private String channelId;
}
