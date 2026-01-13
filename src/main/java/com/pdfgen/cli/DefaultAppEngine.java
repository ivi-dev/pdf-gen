package com.pdfgen.cli;

import java.util.ArrayList;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

class DefaultAppEngine<T> implements AppEngine<T> {

    private final JCommander app;

    private final T args;

    private final AppMetaDataProvider metaProvider;

    DefaultAppEngine(T args) {
        this(args, new StandardAppMetaDataProvider());
    }

    DefaultAppEngine(T args, AppMetaDataProvider metaProvider) {
        this.args = args;
        this.metaProvider = metaProvider;
        app = JCommander.newBuilder().addObject(this.args).build();
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
    public String getUsage(){
        app.setProgramName("pdfgen");
        var sb = new StringBuilder();
        app.usage(sb);
        return sb.toString();
    }

    @Override
    public String getMetaData() {
        return metaProvider.getMetaData();
    }

    @Override
    public T parseArgs(String[] args) throws ParameterException {
        app.parse(normalizeArgs(args));
        return this.args;
    }

}
