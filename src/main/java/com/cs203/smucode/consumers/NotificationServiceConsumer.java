package com.cs203.smucode.consumers;

import com.cs203.smucode.dto.NotificationDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

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
    void streamNotifications(NotificationDTO notificationDTO);
}
