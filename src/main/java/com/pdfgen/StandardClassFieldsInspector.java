package com.pdfgen;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

class StandardClassFieldsInspector implements ClassFieldsInspector {

    private Object targetObj;
    
    private Class<? extends Annotation> annotationClass;
    
    private FieldValueGetter fieldValueGetter;

    private final Predicate<? super Field> nonBlankParams = (field) -> {
        try {
            if (field.isAnnotationPresent(annotationClass)) {
                field.setAccessible(true); 
                var value = fieldValueGetter.get(field, targetObj);
                if (value instanceof Boolean)
                    return (Boolean) value;
                return value != null;
            } 
            return false;
        } catch (IllegalArgumentException | IllegalAccessException e) {
            return false;
        }
    };

    private final Function<? super Field, String> fieldToString = (field) -> {
        try {
            var fieldValue = fieldValueGetter.get(field, targetObj);
            field.setAccessible(true); 
            var fieldName = field.getName();
            if (fieldValue instanceof List) {
                var list = ((List<?>) fieldValue).stream().map((value) -> 
                    String.format("--%s=%s", fieldName, value)
                ).toList();
                return String.join(", ", list);
            } else if (fieldValue instanceof Boolean) {
                return String.format("--%s", fieldName);
            }
            return String.format("--%s=%s", fieldName, fieldValue);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            return "";
        }
    };

    StandardClassFieldsInspector(
        Object targetObj,
        Class<? extends Annotation> annotationClass
    ) {
        this(
            targetObj,
            annotationClass,
            (field, obj) -> field.get(obj)
        );
    }

    StandardClassFieldsInspector(
        Object targetObj,
        Class<? extends Annotation> annotationClass,
        FieldValueGetter fieldValueGetter
    ) {
        this.targetObj = targetObj;
        this.annotationClass = annotationClass;
        this.fieldValueGetter = fieldValueGetter;
    }

    @Override
    public String[] notNullFields() {
        return Arrays.stream(targetObj.getClass().getDeclaredFields())
                     .filter(nonBlankParams)
                     .map(fieldToString)
                     .filter((str) -> !str.isBlank())
                     .toArray(String[]::new);
    }

}