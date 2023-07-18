package com.lineate.mdzinarishvili.reminderapp.dto;

import com.lineate.mdzinarishvili.reminderapp.enums.CategoryType;
import com.lineate.mdzinarishvili.reminderapp.enums.RecurrenceType;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ReminderRequest {
  private String title;
  private RecurrenceType recurrence;
  private LocalDate date;
  private LocalTime time;
  private byte[] attachment;
  private int priority;
  private CategoryType category;
  private String userEmail;
  private String userUsername;
  private List<String> labels = Collections.emptyList();
}
