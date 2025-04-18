package com.vuhlog.money_keeper.service.ServiceImpl;

import com.vuhlog.money_keeper.common.UserCommon;
import com.vuhlog.money_keeper.dao.NotificationRepository;
import com.vuhlog.money_keeper.dto.request.NotificationRequest;
import com.vuhlog.money_keeper.dto.response.NotificationResponse;
import com.vuhlog.money_keeper.entity.Notification;
import com.vuhlog.money_keeper.exception.AppException;
import com.vuhlog.money_keeper.exception.ErrorCode;
import com.vuhlog.money_keeper.mapper.NotificationMapper;
import com.vuhlog.money_keeper.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final UserCommon userCommon;
    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    @Override
    public Page<NotificationResponse> getAllNotifications(Integer pageNumber, Integer pageSize) {
        Sort sortable = Sort.by("createdAt").descending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sortable);
        return notificationRepository.findAll(pageable).map(notificationMapper::toNotificationResponse);
    }

    @Override
    public NotificationResponse createNotification(NotificationRequest request) {
        Notification notification = notificationMapper.toNotification(request);
        notification.setUser(userCommon.getMyUserInfo());
        return notificationMapper.toNotificationResponse(notificationRepository.save(notification));
    }

    @Override
    public String updateReadStatus(String id, Integer readStatus) {
        Notification notification = notificationRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.NOTIFICATION_NOT_EXISTED));
        notification.setReadStatus(readStatus);
        return "update read status successfully";
    }

    @Override
    public void deleteNotification(String id) {
        Notification notification = notificationRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.NOTIFICATION_NOT_EXISTED));
        notificationRepository.deleteById(id);
    }
}
