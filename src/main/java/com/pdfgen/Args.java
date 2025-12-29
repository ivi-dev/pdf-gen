package com.pdfgen;

import java.io.File;
import java.util.List;
import java.util.Locale;

import com.beust.jcommander.Parameter;
import com.pdfgen.converters.FontConverter;
import com.pdfgen.converters.FontDeclaration;
import com.pdfgen.converters.LocaleConverter;

class Args {

    @Parameter(
        names = {"--template", "-i"}, 
        description = "A path to an HTML template to use for the PDF generation."
    )
    private String template;

    @Parameter(
        names = {"--data", "-d"}, 
        description = "Path to a file containing dynamic data to be " +
                      "inserted into the generated document"
    )
    private File data;

    @Parameter(
        names = {"--output", "-o"}, 
        description = "Path to the generated PDF document, e.g. \"/path/to/My-Document.pdf\". " +
                      "If you specify a path to a directory (\"/path/to/dir\"), the generated " +
                      "document will be given a default name and will be placed in that directory, " +
                      "e.g. /path/to/dir/Document.pdf."
    )
    private String output;

    @Parameter(
        names = {"--locale", "-l"}, 
        converter = LocaleConverter.class, 
        description = "Format dates and numbers in the generated document according to this locale. " +
                      "Examples of valid formats: \"en\", \"en-US\", \"en-840\"."
    )
    private Locale locale = Locale.getDefault();

    @Parameter(
        names = {"--font", "-f"}, 
        converter = FontConverter.class, 
        variableArity = true,
        description = "Name and path to a custom font, surrounded by duoble quotes and separated " +
                      "by a space. The font's name has to match the one used in the template's " +
                      "CSS 'font-family' directive. E.g., if you had .some-element { font-family: 'MyCustomFont'; }" +
                      "in your template's styles, then you need to provide " +
                      "--font \"MyCustomFont /path/to/custom-font.(ttf)|(otf) " +
                      "in the command-line. You can specify multiple fonts by repeating this parameter. " +
                      "Example, if you are using two different font families in your template " +
                      "(e.g. 'Regular' and 'Thick'), then you need to provide both of those like this: " +
                      "--font \"Regular /path/to/regular-font.(ttf)|(otf)\" " +
                      "--font \"Thick /path/to/thick-font.(ttf)|(otf)\"."
    )
    private List<FontDeclaration> font;

    @Parameter(
        names = {"--verbose", "-v"}, 
        description = "Print details as the program's going through its operational stages."
    )
    private boolean verbose = false;

    Args() { }

    Args(
        String template, 
        File data, 
        String output, 
        Locale locale, 
        List<FontDeclaration> font,
        boolean verbose
    ) {
        this.template = template;
        this.data = data;
        this.output = output;
        this.locale = locale;
        this.font = font;
        this.verbose = verbose;
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

    boolean getVerbose() {
        return verbose;
    }

}