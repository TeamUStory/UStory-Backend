package com.elice.ustory.global.util;

import com.elice.ustory.global.exception.ErrorCode;
import com.elice.ustory.global.exception.model.ValidationException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@JsonComponent
public class CustomLocalDateDeserializer extends JsonDeserializer<LocalDate> {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    @Override
    public LocalDate deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String date = p.getText();
        try {
            return LocalDate.parse(date, formatter);
        } catch (DateTimeParseException e) {
            throw new ValidationException("전송하는 날짜의 형식이 잘못되었습니다. 날짜의 형식은 yyyy/MM/dd 입니다.", ErrorCode.VALIDATION_PARAMETER_EXCEPTION);
        }
    }
}
