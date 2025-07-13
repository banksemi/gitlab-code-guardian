package kr.easylab.gitlab_code_guardian.llm.service;

import io.swagger.v3.core.converter.ResolvedSchema;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class GoogleSchemaMappingServiceTest {
    @Autowired
    private GoogleSchemaMappingService googleSchemaMappingService;

    @Autowired
    private SchemaExtractionService schemaExtractionService;

    class AAItem {
        public String zzz;
        public String name;
    }
    class TestClass {
        public List<AAItem> aaItems;
    }

    @Test
    void mapSchema_shouldReturnMappedSchema() {
        // Given
        ResolvedSchema resolvedSchema = schemaExtractionService.extractSchema(TestClass.class);


        // When
        Map<String, Object> result = googleSchemaMappingService.mapToGoogleSchema(resolvedSchema.schema, resolvedSchema.referencedSchemas);

        // Then
        assertNotNull(result);
        assertEquals("object", result.get("type"));

        // Check properties exist
        assertTrue(result.containsKey("properties"));
        Map<String, Object> properties = (Map<String, Object>) result.get("properties");
        assertNotNull(properties);

        // Check aaItems property
        assertTrue(properties.containsKey("aaItems"));
        Map<String, Object> aaItemsProperty = (Map<String, Object>) properties.get("aaItems");
        assertNotNull(aaItemsProperty);
        assertEquals("array", aaItemsProperty.get("type"));

        // Check items schema for aaItems array
        assertTrue(aaItemsProperty.containsKey("items"));
        Map<String, Object> itemsSchema = (Map<String, Object>) aaItemsProperty.get("items");
        assertNotNull(itemsSchema);
        assertEquals("object", itemsSchema.get("type"));

        // Check AAItem properties
        assertTrue(itemsSchema.containsKey("properties"));
        Map<String, Object> aaItemProperties = (Map<String, Object>) itemsSchema.get("properties");
        assertNotNull(aaItemProperties);

        // Check name property in AAItem
        assertTrue(aaItemProperties.containsKey("name"));
        Map<String, Object> nameProperty = (Map<String, Object>) aaItemProperties.get("name");
        assertNotNull(nameProperty);
        assertEquals("string", nameProperty.get("type"));

        // Check zzz property in AAItem
        assertTrue(aaItemProperties.containsKey("zzz"));
        Map<String, Object> zzzProperty = (Map<String, Object>) aaItemProperties.get("zzz");
        assertNotNull(zzzProperty);
        assertEquals("string", zzzProperty.get("type"));

        // Check propertyOrdering is included since AAItem has multiple fields
        assertTrue(itemsSchema.containsKey("propertyOrdering"));
        List<String> propertyOrdering = (List<String>) itemsSchema.get("propertyOrdering");
        assertNotNull(propertyOrdering);
        assertEquals(2, propertyOrdering.size());
        assertEquals("zzz", propertyOrdering.get(0));
        assertEquals("name", propertyOrdering.get(1));
    }

}
