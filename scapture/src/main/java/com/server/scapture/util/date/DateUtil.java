package com.server.scapture.util.date;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtil {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // LocalDateTime을 문자열로 변환하는 메서드
    public static String formatLocalDateTime(LocalDateTime dateTime) {
        return dateTime.format(formatter);
    }
}
