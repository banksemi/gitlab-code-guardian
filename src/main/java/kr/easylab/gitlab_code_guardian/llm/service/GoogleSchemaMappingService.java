package kr.easylab.gitlab_code_guardian.llm.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.google.common.base.CaseFormat;
import io.swagger.v3.oas.models.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GoogleSchemaMappingService {
    private final ObjectMapper objectMapper;

    private final Map<String, Function<Schema<?>, Object>> fieldAccessors = Map.ofEntries(
            Map.entry("type", Schema::getType),
            Map.entry("format", Schema::getFormat),
            Map.entry("description", Schema::getDescription),
            Map.entry("nullable", Schema::getNullable),
            Map.entry("enum", Schema::getEnum),
            Map.entry("maxItems", Schema::getMaxItems),
            Map.entry("minItems", Schema::getMinItems),
            Map.entry("required", Schema::getRequired)
    );

    public Map<String, Object> mapToGoogleSchema(Schema<?> schema) {
        return mapToGoogleSchema(schema, null);
    }

    public Map<String, Object> mapToGoogleSchema(Schema<?> schema, Map<String, Schema> resolvedSchemas) {
        Map<String, Object> map = new HashMap<>();
        for (Map.Entry<String, Function<Schema<?>, Object>> entry : fieldAccessors.entrySet()) {
            String fieldName = entry.getKey();
            Object value = entry.getValue().apply(schema);
            if (value != null) {
                map.put(fieldName, value);
            }
        }

        // If no explicit type is set but schema has properties, it's an object
        if (schema.getType() == null && schema.getProperties() != null && !schema.getProperties().isEmpty()) {
            map.put("type", "object");
        }

        // If schema has no type and no properties, but we have resolved schemas, try to find a match
        if (schema.getType() == null && (schema.getProperties() == null || schema.getProperties().isEmpty()) && resolvedSchemas != null) {
            // Look for a schema that matches this one in resolved schemas
            for (Map.Entry<String, Schema> entry : resolvedSchemas.entrySet()) {
                Schema<?> resolvedSchema = entry.getValue();
                if (resolvedSchema.getProperties() != null && !resolvedSchema.getProperties().isEmpty()) {
                    map.put("type", "object");
                    Map<String, Object> properties = new HashMap<>();
                    for (Map.Entry<String, Schema> propEntry: resolvedSchema.getProperties().entrySet()) {
                        properties.put(convertFieldName(propEntry.getKey()), mapToGoogleSchema(propEntry.getValue(), resolvedSchemas));
                    }
                    map.put("properties", properties);

                    // Add propertyOrdering if object has multiple fields
                    if (resolvedSchema.getProperties().size() > 1) {
                        List<String> propertyOrdering = resolvedSchema.getProperties().keySet().stream()
                                .map(this::convertFieldName)
                                .collect(Collectors.toList());
                        map.put("propertyOrdering", propertyOrdering);
                    }

                    break; // Use the first matching resolved schema
                }
            }
        }

        if (schema.getProperties() != null) {
            Map<String, Object> properties = new HashMap<>();
            for (Map.Entry<String, Schema> entry: schema.getProperties().entrySet()) {
                properties.put(convertFieldName(entry.getKey()), mapToGoogleSchema(entry.getValue(), resolvedSchemas));
            }
            map.put("properties", properties);

            // Add propertyOrdering if object has multiple fields
            if (schema.getProperties().size() > 1) {
                List<String> propertyOrdering = schema.getProperties().keySet().stream()
                        .map(this::convertFieldName)
                        .collect(Collectors.toList());
                map.put("propertyOrdering", propertyOrdering);
            }
        }
        if (schema.getItems() != null) {
            map.put("items", mapToGoogleSchema(schema.getItems(), resolvedSchemas));
        }
        return map;
    }

    private String convertFieldName(String fieldName) {
        PropertyNamingStrategy strategy = objectMapper.getPropertyNamingStrategy();
        if (strategy instanceof PropertyNamingStrategies.SnakeCaseStrategy) {
            return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, fieldName);
        }
        return fieldName;
    }

}
