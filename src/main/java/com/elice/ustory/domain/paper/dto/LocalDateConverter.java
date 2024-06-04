package com.elice.ustory.domain.paper.dto;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

public class LocalDateConverter extends JsonSerializer<LocalDate> {
    @Override
    public void serialize(LocalDate value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        String formattedDate = value.format(formatter);
        String dayOfWeek = value.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.KOREAN);
        gen.writeString(formattedDate + " (" + dayOfWeek + ")");
    }
}
