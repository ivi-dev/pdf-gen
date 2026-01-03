package com.pdfgen.cli;

import com.beust.jcommander.ParameterException;

public class DefaultArgParser<T> implements ArgParser<T> {

    private final AppEngine<T> app;

    private final T args;

    public DefaultArgParser(T args) {
        this.args = args;
        this.app = new DefaultAppEngine<>(this.args);
    }

    public DefaultArgParser(T args, AppEngine<T> appEngine) {
        this.args = args;
        this.app = appEngine;
    }

    @Override
    public String getUsage() {
        return app.getUsage();
    }

    @Override
    public T parse(String[] args) throws ParameterException {
        return app.parseArgs(args);
    }

}