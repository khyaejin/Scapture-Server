package com.server.scapture.stadium.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
@AllArgsConstructor
@Builder
public class GetMainInfoResponseDto {
    private GetMainInfoPopularDto popular;
    private List<GetMainInfoStadiumDto> stadiums;
}
