package com.gobliip.jpa.converters;

import java.sql.Timestamp;
import java.time.Instant;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class InstantPersistenceConverter implements
        AttributeConverter<Instant, Timestamp> {

    @Override
    public Timestamp convertToDatabaseColumn(Instant attribute) {
        if (attribute == null)
            return null;
        return Timestamp.from(attribute);
    }

    @Override
    public Instant convertToEntityAttribute(Timestamp dbData) {
        if (dbData == null)
            return null;
        return dbData.toInstant();
    }

}
