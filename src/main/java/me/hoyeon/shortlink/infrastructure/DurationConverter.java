package me.hoyeon.shortlink.infrastructure;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.time.Duration;

@Converter(autoApply = false)
public class DurationConverter implements AttributeConverter<Duration, Long> {

  @Override
  public Long convertToDatabaseColumn(Duration attribute) {
    return attribute != null ? attribute.toMinutes() : null;
  }

  @Override
  public Duration convertToEntityAttribute(Long dbData) {
    return dbData != null ? Duration.ofMinutes(dbData) : null;
  }
}
