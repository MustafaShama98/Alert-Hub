package org.example.actionschedulerservice.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.io.IOException;
import java.util.List;

@Converter
public class ConditionConverter implements AttributeConverter<List<List<Integer>>, String> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<List<Integer>> attribute) {
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error converting to JSON", e);
        }
    }

    @Override
    public List<List<Integer>> convertToEntityAttribute(String dbData) {
        try {
            return objectMapper.readValue(dbData, new TypeReference<List<List<Integer>>>() {});
        } catch (IOException e) {
            throw new IllegalArgumentException("Error reading JSON", e);
        }
    }
}