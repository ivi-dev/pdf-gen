package com.pdfgen.cli;

import java.util.List;
import java.util.ResourceBundle;

import com.beust.jcommander.DefaultUsageFormatter;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterDescription;

public class LocalizedUsageFormatter extends DefaultUsageFormatter {

    private final JCommander commander;

    private final ResourceBundle bundle;

    public LocalizedUsageFormatter(
        JCommander commander, 
        ResourceBundle bundle
    ) {
        super(commander);
        this.bundle = bundle;
        this.commander = commander;
    }

    @Override
    public void appendMainLine(
        StringBuilder out, 
        boolean hasOptions, 
        boolean hasCommands, 
        int indentCount, 
        String indent
    ) {
        out.append(bundle.getString("usageHeader") + " ")
           .append(commander.getProgramName() + " ")
           .append(getI18nString(bundle, "usageSynopsisOptions", "-"))
           .append("\n\n")
           .append(s(2) + getI18nString(bundle, "usageTitleOptions", "-") + "\n");
    }

    @Override
    public void appendAllParametersDetails(
        StringBuilder out, 
        int indentCount, 
        String indent, 
        List<ParameterDescription> sortedParameters
    ) {
        var indentSize = 2;
        var params = commander.getParameters();
        params.sort(commander.getParameterDescriptionComparator());
        for (var param : params) {
            out.append(s(indentSize += 2) + param.getNames() + "\n");
            indentSize += 2;
            wrapDescription(out, indentSize, s(indentSize) + param.getDescription());
            out.append("\n");
            var def = param.getDefault();
            if (def != null) {
                out.append(s(indentSize) + bundle.getString("usageDefault") + " ");
                out.append(def.toString() + "\n");
            }
            indentSize -= 4;
        }
    }
    
}
