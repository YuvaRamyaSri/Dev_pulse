package com.devpulse.Service;

import com.devpulse.model.User;
import java.util.Map;

public interface InsightsService {
    Map<String, Object> getLongTermInsights(User user);
}
