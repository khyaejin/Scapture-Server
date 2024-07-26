package com.server.scapture.util.date;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

public class DateUtil {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd(E)", Locale.KOREAN);

    // LocalDateTime을 문자열로 변환하는 메서드
    public static String formatLocalDateTime(LocalDateTime dateTime) {
        return dateTime.format(formatter);
    }

    // 요일을 한글로 변환하는 메서드
    private static String getKoreanDayOfWeek(LocalDateTime dateTime) {
        return dateTime.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.KOREAN);
    }

    // LocalDateTime을 원하는 형식의 문자열로 변환하는 메서드
    public static String formatLocalDateTimeWithKoreanDay(LocalDateTime dateTime) {
        DateTimeFormatter formatterWithoutDay = DateTimeFormatter.ofPattern("yyyy.MM.dd", Locale.KOREAN);
        return dateTime.format(formatterWithoutDay) + "(" + getKoreanDayOfWeek(dateTime) + ")";
    }
}
