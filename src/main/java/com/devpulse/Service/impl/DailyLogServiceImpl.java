package com.devpulse.Service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import com.devpulse.dao.DailyLogRepository;
import com.devpulse.model.DailyLog;
import com.devpulse.model.User;
import com.devpulse.Service.DailyLogService;

@Service
public class DailyLogServiceImpl implements DailyLogService {

    @Autowired
    private DailyLogRepository dailyLogRepository;

    @Override
    public void saveLog(DailyLog log) {
        dailyLogRepository.save(log);
    }

    @Override
    public List<DailyLog> getLogsByUser(User user) {
        return dailyLogRepository.findByUserOrderByLogDateDesc(user);
    }
}
