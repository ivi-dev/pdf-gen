package com.pdfgen.cli;

import com.beust.jcommander.ParameterException;

public class DefaultArgParser<T> implements ArgParser<T> {

    private final AppEngine<T> app;

    private final AppMetaDataProvider metaProvider;

    public DefaultArgParser(T args) {
        this(new DefaultAppEngine<>(args));
    }

    public DefaultArgParser(AppEngine<T> appEngine) {
        this(appEngine, new StandardAppMetaDataProvider());
    }

    public DefaultArgParser(
        AppEngine<T> appEngine, 
        AppMetaDataProvider metaProvider
    ) {
        this.app = appEngine;
        this.metaProvider = metaProvider;
    }

    @Override
    public String getUsage() {
        return app.getUsage();
    }

    @Override
    public T parse(String[] args) throws ParameterException {
        return app.parseArgs(args);
    }

    @Override
    public String getVersion() {
        return metaProvider.getMetaData();
    }

}