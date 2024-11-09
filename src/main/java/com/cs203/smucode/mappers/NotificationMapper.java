package com.cs203.smucode.mappers;

import com.cs203.smucode.constants.NotificationCategory;
import com.cs203.smucode.constants.NotificationType;
import com.cs203.smucode.dtos.notifications.NotificationDTO;
import com.cs203.smucode.models.events.Event;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * @author jered
 * @version 1.0
 * @since 2024-11-09
 *
 * This class is used to map events to notification Data Transfer Objects (DTOs).
 */

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    @Mapping(source = "type", target = "type", qualifiedByName = "notificationTypeToString")
    @Mapping(source = "category", target = "category", qualifiedByName = "notificationCategoryToString")
    NotificationDTO eventToNotificationDTO(Event event);

    @Named("notificationTypeToString")
    default String notificationTypeToString(NotificationType type) {
        return type.toString().toLowerCase();
    }

    @Named("notificationCategoryToString")
    default String notificationCategoryToString(NotificationCategory type) {
        return type.toString().toLowerCase();
    }

}
