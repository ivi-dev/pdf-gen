package com.pdfgen;

import com.pdfgen.cli.DefaultArgParser;
import com.pdfgen.reporting.StandardOutReporter;

public class App {

    private App() { }

    public static void main(String[] args) {
        new Proc().run(
            args, 
            new DefaultArgParser<>(new Args()), 
            (parsedArgs) -> 
                new PDFGenerator(
                    parsedArgs.getTemplate(), 
                    parsedArgs.getData(),
                    parsedArgs.getOutput(), 
                    parsedArgs.getLocale(),
                    parsedArgs.getFont()
                ),
            new StandardOutReporter()
        );
    }
    
}
