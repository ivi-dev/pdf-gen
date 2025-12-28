package com.pdfgen;

import java.io.File;
import java.util.List;
import java.util.Locale;

import com.beust.jcommander.Parameter;
import com.pdfgen.converters.FontConverter;
import com.pdfgen.converters.FontDeclaration;
import com.pdfgen.converters.LocaleConverter;

class Args {

    @Parameter(names = {"--template", "-i"}, description = "A custom PDF template, written in HTML")
    private String template;

    @Parameter(names = {"--data", "-d"}, description = "Input data file path")
    private File data;

    @Parameter(names = {"--output", "-o"}, description = "Output PDF file path")
    private String output;

    @Parameter(
        names = {"--locale", "-l"}, 
        converter = LocaleConverter.class, 
        description = "Localization for dates, times, numbers etc."
    )
    private Locale locale = Locale.getDefault();

    @Parameter(
        names = {"--font", "-f"}, 
        converter = FontConverter.class, 
        variableArity = true,
        description = "Path to a custom font. You can specify multiple fonts by repeating this parameter"
    )
    private List<FontDeclaration> font;

    Args() { }

    Args(
        String template, 
        File data, 
        String output, 
        Locale locale, 
        List<FontDeclaration> font
    ) {
        this.template = template;
        this.data = data;
        this.output = output;
        this.locale = locale;
        this.font = font;
    }

    String getTemplate() {
        return template;
    }

    File getData() {
        return data;
    }

    String getOutput() {
        return output;
    }

    Locale getLocale() {
        return locale;
    }

    List<FontDeclaration> getFont() {
        return font;
    }

}