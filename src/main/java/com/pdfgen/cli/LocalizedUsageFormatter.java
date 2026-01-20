package com.pdfgen.cli;

import java.text.BreakIterator;
import java.util.ResourceBundle;

import com.beust.jcommander.IUsageFormatter;
import com.beust.jcommander.JCommander;

public class LocalizedUsageFormatter implements IUsageFormatter {

    private final JCommander commander;  

    private final ResourceBundle bundle;

    public LocalizedUsageFormatter(
        JCommander commander, 
        ResourceBundle bundle
    ) {
        this.commander = commander;
        this.bundle = bundle;
    }

    @Override
    public String getCommandDescription(String commandName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void usage(String commandName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void usage(StringBuilder out) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void usage(String commandName, StringBuilder out) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void usage(String commandName, StringBuilder out, String indent) {
        throw new UnsupportedOperationException();
    }
    private static final int LINE_WIDTH = 80;

    @Override
    public void usage(StringBuilder out, String indent) {
        var indentSize = printHeader(out);
        printParams(out, indentSize);
    }

    private void printParams(StringBuilder out, int indentSize) {
        var params = commander.getParameters();
        params.sort(commander.getParameterDescriptionComparator());
        for (var param : params) {
            out.append(space(indentSize += 2) + param.getNames() + "\n");
            appendWrapped(out, space(indentSize += 2), param.getDescription());
            out.append("\n");
            var def = param.getDefault();
            if (def != null) {
                out.append(space(indentSize) + bundle.getString("usageDefault") + " ");
                out.append(def.toString() + "\n");
            }
            indentSize -= 4;
        }
    }

    private int printHeader(StringBuilder out) {
        var indentSize = 0;
        out.append(bundle.getString("usageHeader") + " ")
           .append(commander.getProgramName() + " ")
           .append(bundle.getString("usageSynopsisOptions"))
           .append("\n\n")
           .append(space(indentSize += 2) + bundle.getString("usageTitleOptions") + "\n");
        return indentSize;
    }

    private static void appendWrapped(
        StringBuilder sb,
        String indent,
        String text
    ) {
        var boundary = BreakIterator.getLineInstance();
        boundary.setText(text);
        var start = boundary.first();
        var lineLen = 0;
        sb.append(indent);
        for (var end = boundary.next(); 
             end != BreakIterator.DONE; 
             end = boundary.next()) {
            var word = text.substring(start, end);
            if (lineLen + word.length() > LINE_WIDTH) { 
                sb.append('\n').append(indent); 
                lineLen = 0; 
            }
            sb.append(word);
            lineLen += word.length();
            start = end;
        }
    }

    private static String space(int times) {
        var s = "";
        for (var i = 0; i < times; i++)
            s += " ";
        return s;
    }
    
}
