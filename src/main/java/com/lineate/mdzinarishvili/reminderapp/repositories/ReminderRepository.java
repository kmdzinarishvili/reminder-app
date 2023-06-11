package com.lineate.mdzinarishvili.reminderapp.repositories;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ReminderRepository  {

    final Map<Long, String> reminderMap = new HashMap<>();

    public List<String> findAll(){
        return new ArrayList<>(reminderMap.values());
    }


}