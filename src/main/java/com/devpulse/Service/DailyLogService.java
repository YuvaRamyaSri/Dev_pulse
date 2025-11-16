package com.devpulse.Service;

import com.devpulse.model.DailyLog;
import com.devpulse.model.User;
import java.util.List;

public interface DailyLogService {
    void saveLog(DailyLog log);
    List<DailyLog> getLogsByUser(User user);
}
