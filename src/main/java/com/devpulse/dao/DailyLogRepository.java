package com.devpulse.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import com.devpulse.model.DailyLog;
import com.devpulse.model.User;
import java.util.List;

public interface DailyLogRepository extends JpaRepository<DailyLog, Long> {
    List<DailyLog> findByUserOrderByLogDateDesc(User user);
}