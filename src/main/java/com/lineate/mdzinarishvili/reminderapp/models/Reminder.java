package com.lineate.mdzinarishvili.reminderapp.models;

import com.lineate.mdzinarishvili.reminderapp.enums.RecurrenceType;
import lombok.*;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Reminder {
    private Long id;
    private String title;
    private RecurrenceType recurrence;
    private Date date;
    private byte[] attachment;

}
