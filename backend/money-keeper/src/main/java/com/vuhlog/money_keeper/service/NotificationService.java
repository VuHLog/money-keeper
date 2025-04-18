package com.vuhlog.money_keeper.service;

import com.vuhlog.money_keeper.dto.request.NotificationRequest;
import com.vuhlog.money_keeper.dto.response.NotificationResponse;
import org.springframework.data.domain.Page;

public interface NotificationService {
    Page<NotificationResponse> getAllNotifications(Integer pageNumber, Integer pageSize);

    NotificationResponse createNotification(NotificationRequest request);

    String updateReadStatus(String id, Integer readStatus);

    void deleteNotification(String id);
}
