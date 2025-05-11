package com.project.spring_project.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class JsonUtils  {
    private static final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private static final ObjectMapper mapperNotNulls = new ObjectMapper()
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    /**
     * Converts an object to its JSON string representation.
     *
     * @param o the object to convert
     * @return the JSON string representation of the object
     * @throws RuntimeException if the conversion fails
     */
    public static String objectToJson(Object o) {
        try {
            return mapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert object to JSON: "+e, e);
        }
    }

    /**
     * Converts an object to its JSON string representation, excluding null values.
     *
     * @param o the object to convert
     * @return the JSON string representation of the object, excluding null values
     * @throws RuntimeException if the conversion fails
     */
    public static String objectToJsonNotNulls(Object o) {
        try {
            return mapperNotNulls.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert object to JSON: "+e, e);
        }
    }
}
