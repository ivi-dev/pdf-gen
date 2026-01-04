package com.pdfgen;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@Retention(RetentionPolicy.RUNTIME)
@interface MockAnnotation { }

class MockObject {

    @MockAnnotation
    private String strField = "Hello, world!";

    @MockAnnotation
    private Integer nullField = null;

    @MockAnnotation
    private boolean boolFalseField = false;

    @MockAnnotation
    private boolean boolTrueField = true;

    @MockAnnotation
    private List<Integer> listField = List.of(1, 2, 3);

    @SuppressWarnings("unused")
    private String notAnnotatedField = "not annotated";

}

public class StandardClassFieldsInspectorTest {

    private MockObject obj;

    @BeforeEach
    void setUp() {
        obj = new MockObject();
    }

    @Test
    void notNullFieldsReturnsAnnotatedNonNullFields() {
        var inspector = new StandardClassFieldsInspector(
            obj,
            MockAnnotation.class
        );
        assertArrayEquals(new String[] {
            "--strField=Hello, world!",
            "--boolTrueField",
            "--listField=1, " +
            "--listField=2, " +
            "--listField=3",
        }, inspector.notNullFields());
    }

    @Test
    void notNullFieldsReturnsCorrectWhenFieldValueGetterThrowsTryingToCheckIfFieldIsNotNull() 
        throws IllegalArgumentException, IllegalAccessException {
        var mockFieldValueGetter = mock(FieldValueGetter.class);
        var callCtr = new AtomicInteger(0);
        doAnswer(invocation -> {
            Field field = invocation.getArgument(0);
            return switch (field.getName()) {
                case "strField"       -> "Hello, world!";
                case "nullField"      -> null;
                case "boolFalseField" -> false;
                case "boolTrueField"  -> true;
                case "listField" -> {
                    var nCalls = callCtr.incrementAndGet();
                    if (nCalls == 1) {
                        throw new IllegalAccessException();
                    } else {
                        yield true;
                    }
                }
                default -> null;
            };
        }).when(mockFieldValueGetter).get(
            any(Field.class), 
            any(Object.class)
        );
        var inspector = new StandardClassFieldsInspector(
            obj,
            MockAnnotation.class,
            mockFieldValueGetter
        );
        assertArrayEquals(new String[] {
            "--strField=Hello, world!",
            "--boolTrueField"
        }, inspector.notNullFields());
    }

    @Test
    void notNullFieldsReturnsCorrectWhenFieldValueGetterThrowsTryingToConvertFieldToString() 
        throws IllegalArgumentException, IllegalAccessException {
        var mockFieldValueGetter = mock(FieldValueGetter.class);
        var callCtr = new AtomicInteger(0);
        doAnswer(invocation -> {
            Field field = invocation.getArgument(0);
            return switch (field.getName()) {
                case "strField"       -> "Hello, world!";
                case "nullField"      -> null;
                case "boolFalseField" -> false;
                case "boolTrueField"  -> true;
                case "listField" -> {
                    var nCalls = callCtr.incrementAndGet();
                    if (nCalls == 1) {
                        yield true;
                    } else {
                        throw new IllegalAccessException();
                    }
                }
                default -> null;
            };
        }).when(mockFieldValueGetter).get(
            any(Field.class), 
            any(Object.class)
        );
        var inspector = new StandardClassFieldsInspector(
            obj,
            MockAnnotation.class,
            mockFieldValueGetter
        );
        assertArrayEquals(new String[] {
            "--strField=Hello, world!",
            "--boolTrueField"
        }, inspector.notNullFields());
    }

}
