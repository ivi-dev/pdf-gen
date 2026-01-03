package com.pdfgen;

import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.pdfgen.converters.FontConverter;
import com.pdfgen.converters.FontDeclaration;
import com.pdfgen.converters.LocaleConverter;

@Parameters(resourceBundle = "i18n.Messages")
class Args {

    @Parameter(
        names = {"--template", "-t"}, 
        descriptionKey = "argHTMLTemplate",
        order = 0
    )
    private String template;

    @Parameter(
        names = {"--data", "-d"},
        descriptionKey = "argDynamicDataFile",
        order = 1
    )
    private File data;

    @Parameter(
        names = {"--output", "-o"},
        descriptionKey = "argOutputFilePath",
        order = 2
    )
    private String output = "Document.pdf";

    @Parameter(
        names = {"--locale", "-l"}, 
        converter = LocaleConverter.class, 
        descriptionKey = "argLocale",
        order = 3
    )
    private Locale locale = Locale.getDefault();

    @Parameter(
        names = {"--font", "-f"}, 
        converter = FontConverter.class, 
        variableArity = true,
        descriptionKey = "argFont",
        order = 4
    )
    private List<FontDeclaration> font;

    @Parameter(
        names = "--verbose", 
        descriptionKey = "argVerbose",
        order = 5
    )
    private boolean verbose = false;

    @Parameter(
        names = {"--help", "-h"}, 
        descriptionKey = "argHelp",
        order = 5,
        help = true
    )
    private boolean help = false;

    private static final Function<Args, ClassFieldsInspector> DEFAULT_FIELD_INSPECTOR_SUPPLIER =
        (args) -> new StandardClassFieldsInspector(args, Parameter.class);

    private ClassFieldsInspector fieldInspector = DEFAULT_FIELD_INSPECTOR_SUPPLIER.apply(this);

    Args() { }

    Args(
        String template, 
        File data, 
        String output, 
        Locale locale, 
        List<FontDeclaration> font,
        boolean verbose,
        boolean help
    ) {
        this(
            template,
            data,
            output,
            locale,
            font,
            verbose,
            help,
            DEFAULT_FIELD_INSPECTOR_SUPPLIER
        );
    }

    Args(
        String template, 
        File data, 
        String output, 
        Locale locale, 
        List<FontDeclaration> font,
        boolean verbose,
        boolean help,
        Function<Args, ClassFieldsInspector> fieldInspectorSupplier
    ) {
        this.template = template;
        this.data = data;
        this.output = output;
        this.locale = locale;
        this.font = font;
        this.verbose = verbose;
        this.help = help;
        this.fieldInspector = fieldInspectorSupplier.apply(this);
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

    boolean getHelp() {
        return help;
    }

    @Override
    public String toString() {
        return String.join(", ", fieldInspector.notNullFields());
    }

}