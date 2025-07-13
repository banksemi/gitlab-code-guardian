package kr.easylab.gitlab_code_guardian.llm.service;

import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.core.converter.ResolvedSchema;
import org.springframework.stereotype.Service;


@Service
public class SchemaExtractionService {
    public ResolvedSchema extractSchema(Class<?> clazz) {
        return ModelConverters.getInstance().readAllAsResolvedSchema(new AnnotatedType(clazz));
    }
}
