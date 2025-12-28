package com.pdfgen.cli;

import com.beust.jcommander.ParameterException;

public interface ArgParser<T> {

    void printUsage();

    T parse(String[] args) throws ParameterException;
    
}
