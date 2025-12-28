package com.pdfgen.converters;

import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.ParameterException;

public class FontConverter implements IStringConverter<FontDeclaration> {

    @Override
    public FontDeclaration convert(String value) {
        var excMsg = "A font declaration must contain both a name and a file path!";
        try {
            var parts = value.split(" ");
            if (parts.length < 2) {
                throw new ParameterException(excMsg);
            }
            return new FontDeclaration(parts[0], parts[1]);
        } catch (NullPointerException e) {
            throw new ParameterException(excMsg, e);
        }
    }

}
