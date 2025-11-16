package com.devpulse.Service;

import com.devpulse.model.User;
import java.util.Map;

public interface WeeklySummaryService {
    Map<String, Object> getWeeklySummary(User user);
}