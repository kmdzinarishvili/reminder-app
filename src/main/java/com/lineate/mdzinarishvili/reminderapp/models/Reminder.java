package com.lineate.mdzinarishvili.reminderapp.models;

import com.lineate.mdzinarishvili.reminderapp.enums.RecurrenceType;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;


public class Reminder {
    private Long id;
    private String title;
    private RecurrenceType recurrence;
    private LocalDate date;
    private LocalTime time;
    private byte[] attachment;

    public Reminder() {
    }

    public Reminder(Long id, String title, RecurrenceType recurrence, LocalDate date, LocalTime time, byte[] attachment) {
        this.id = id;
        this.title = title;
        this.recurrence = recurrence;
        this.date = date;
        this.time = time;
        this.attachment = attachment;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public RecurrenceType getRecurrence() {
        return recurrence;
    }

    public void setRecurrence(RecurrenceType recurrence) {
        this.recurrence = recurrence;
    }
//    public void setRecurrence(String recurrence) {
//        this.recurrence = recurrence;
//    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public byte[] getAttachment() {
        return attachment;
    }

    public void setAttachment(byte[] attachment) {
        this.attachment = attachment;
    }

    @Override
    public String toString() {
        return "Reminder{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", recurrence=" + recurrence +
                ", date=" + date +
                ", time=" + time +
                ", attachment=" + Arrays.toString(attachment) +
                '}';
    }
}
