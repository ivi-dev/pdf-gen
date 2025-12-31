package com.pdfgen;

import java.lang.reflect.Field;

@FunctionalInterface
interface FieldValueGetter {

    Object get(Field field, Object obj) 
        throws IllegalArgumentException, IllegalAccessException;
    
}