package com.pdfgen.cli;

import com.beust.jcommander.ParameterException;

public interface ArgParser<T> {

    String getUsage();

    T parse(String[] args) throws ParameterException;
    
}
