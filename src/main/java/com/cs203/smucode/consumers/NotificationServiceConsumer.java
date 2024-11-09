package com.cs203.smucode.consumers;

import com.cs203.smucode.dtos.notifications.NotificationDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author gav
 * @version 1.0
 * @since 2024-11-10
 *
 * This class is used to consume API endpoints exposed by notification microservice.
 */

@FeignClient(name = "notification-service",
        url = "${services.notification.url}")
public interface NotificationServiceConsumer {

    @PostMapping("/stream")
    void streamNotifications(@RequestBody NotificationDTO notificationDTO);
}
