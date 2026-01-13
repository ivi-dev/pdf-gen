package com.pdfgen.cli;

import com.beust.jcommander.ParameterException;

public class DefaultArgParser<T> implements ArgParser<T> {

    private final AppEngine<T> app;

    public DefaultArgParser(T args) {
        this(new DefaultAppEngine<>(args));
    }

    public DefaultArgParser(AppEngine<T> appEngine) {
        this.app = appEngine;
    }

    @Override
    public String getUsage() {
        return app.getUsage();
    }

    @Override
    public String getMetaData() {
        return app.getMetaData();
    }

    @Override
    public T parse(String[] args) throws ParameterException {
        return app.parseArgs(args);
    }

}