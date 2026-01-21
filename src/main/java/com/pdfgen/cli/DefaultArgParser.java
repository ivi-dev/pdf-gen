package com.pdfgen.cli;

import java.util.ArrayList;
import java.util.ResourceBundle;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

public class DefaultArgParser<T> implements ArgParser<T> {

    private final T args;

    private final JCommander commander;

    private final AppMetaDataProvider meta;

    public DefaultArgParser(T args) {
        this.args = args;
        this.meta = new StandardAppMetaDataProvider();
        this.commander = initCommander(args, meta);
    }

    private JCommander initCommander(T args, AppMetaDataProvider meta) {
        var commander = JCommander.newBuilder()
                                  .addObject(args)
                                  .build();
        commander.setProgramName(meta.getShortName());
        commander.setUsageFormatter(
            new LocalizedUsageFormatter(
                commander, 
                ResourceBundle.getBundle(
                    "i18n.Messages"
                )
            )
        );
        return commander;
    }

    private static String[] normalizeArgs(String[] args) {
        var normalized = new ArrayList<String>();
        for (String arg : args) {
            if (arg.contains("=")) {
                String[] parts = arg.split("=", 2);
                normalized.add(parts[0]);
                normalized.add(parts[1]);
            } else {
                normalized.add(arg);
            }
        }
        return normalized.toArray(new String[0]);
    }

    @Override
    public String getUsage() {
        var sb = new StringBuilder();
        commander.usage(sb);
        return sb.toString();
    }

    @Override
    public String getMetaData() {
        return meta.getMetaData();
    }

    @Override
    public T parse(String[] args) throws ParameterException {
        commander.parse(normalizeArgs(args));
        return this.args;
    }

}