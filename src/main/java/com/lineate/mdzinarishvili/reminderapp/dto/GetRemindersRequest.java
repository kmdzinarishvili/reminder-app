package com.lineate.mdzinarishvili.reminderapp.dto;

import com.lineate.mdzinarishvili.reminderapp.enums.TimePeriod;
import lombok.Data;


@Data
public class GetRemindersRequest {
  private TimePeriod timePeriod;
}
