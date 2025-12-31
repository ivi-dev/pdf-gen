package com.pdfgen.converters;

public record FontDeclaration(String name, String path) { 
    
    @Override
    public String toString() {
        return String.format("\"%s %s\"", name, path);
    }

}
