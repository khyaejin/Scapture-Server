package com.server.scapture.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Setter;

@Setter
@AllArgsConstructor
@Builder
public class BananaGetDetailDto {
    private int balance;
    private boolean isSubscribed;
}
