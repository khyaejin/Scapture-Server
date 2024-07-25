package com.server.scapture.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@Builder
public class BananaBalanceResponseDto {
    private int balance;
    private boolean isSubscribed;
}
