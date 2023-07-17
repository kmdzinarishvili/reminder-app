package com.lineate.mdzinarishvili.reminderapp.models;

import com.lineate.mdzinarishvili.reminderapp.dao.ReminderDAO;
import com.lineate.mdzinarishvili.reminderapp.dto.ReminderRequest;
import com.lineate.mdzinarishvili.reminderapp.enums.CategoryType;
import com.lineate.mdzinarishvili.reminderapp.enums.RecurrenceType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import lombok.*;

import java.util.Date;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Reminder implements Comparable<Reminder> {
  private Long id;
  private String title;
  private RecurrenceType recurrence;
  private LocalDateTime date;
  private byte[] attachment;
  private int priority;
  private CategoryType category;
  private List<Label> labels = new ArrayList<>();
  private User user;
  private Boolean acceptanceStatus;
  private LocalDate whichCompleted;


  public Reminder(Long id, String title, LocalDateTime date, RecurrenceType recurrence,
                  byte[] attachment, int priority, CategoryType category,
                  Boolean acceptanceStatus, LocalDate whichCompleted, Label label) {
    this.id = id;
    this.title = title;
    this.date = date;
    this.recurrence = recurrence;
    this.attachment = attachment;
    this.priority = priority;
    this.category = category;
    this.acceptanceStatus = acceptanceStatus;
    this.whichCompleted = whichCompleted;
    this.labels.add(label);
  }

  public Reminder(ReminderRequest reminderRequest) {
    this.title = reminderRequest.getTitle();
    if (reminderRequest.getTime() != null) {
      this.date =
          LocalDateTime.of(reminderRequest.getDate(), reminderRequest.getTime());
    } else {
      this.date = LocalDateTime.of(reminderRequest.getDate(), LocalTime.of(0, 0, 0));
    }
    this.recurrence = reminderRequest.getRecurrence();
    this.attachment = reminderRequest.getAttachment();
    this.priority = reminderRequest.getPriority();
    this.category = reminderRequest.getCategory();
  }

  public void addLabels(List<Label> labels){
    this.labels = Stream.concat(this.labels.stream(), labels.stream()).toList();
  }
  @Override
  public int compareTo(Reminder reminder) {
    if (this.getDate() == null || reminder.getDate() == null) {
      return 0;
    }
    return getDate().compareTo(reminder.getDate());
  }
}
