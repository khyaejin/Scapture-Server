package com.server.scapture.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                // PK
    @ManyToOne
    @JoinColumn(name = "field_id")
    private Field field;            // 구장 FK
    private LocalDateTime startDate;// 이용 시간(시작)
    private LocalDateTime endDate;  // 이용 시간(종료)
    @ColumnDefault("false")
    private Boolean isReserved;     // 예약 여부
    private int price;              // 가격

    public String convertHourAndMin() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        String formattedStartDate = startDate.format(formatter);
        String formattedEndDate = endDate.format(formatter);
        return formattedStartDate + "~" + formattedEndDate;
    }
    public String convertAll() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd.E", Locale.KOREA);
        return startDate.format(formatter);
    }
    public String convertMonthAndDay() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM.dd.E", Locale.KOREA);
        return startDate.format(formatter);
    }
}
