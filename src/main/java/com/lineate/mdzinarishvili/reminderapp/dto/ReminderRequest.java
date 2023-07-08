package com.lineate.mdzinarishvili.reminderapp.dto;

import com.lineate.mdzinarishvili.reminderapp.enums.RecurrenceType;
import lombok.*;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ReminderRequest {
    private String title;
    private RecurrenceType recurrence;
    private Date date;
    private byte[] attachment;
}
