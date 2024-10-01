package com.cs203.smucode.converters;

import com.cs203.smucode.constants.SignupStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class SignupStatusConverter implements AttributeConverter<SignupStatus, String> {

    @Override
    public String convertToDatabaseColumn(SignupStatus attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.toString(); // Enum's toString() returns lowercase value
    }

    @Override
    public SignupStatus convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return null;
        }
        return SignupStatus.fromValue(dbData); // Converts database value to enum
    }
}
