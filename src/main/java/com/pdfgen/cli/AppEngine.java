package com.pdfgen.cli;

import com.beust.jcommander.ParameterException;

interface AppEngine<T> {

    void printUsage();

    T parseArgs(String[] args) throws ParameterException;
    
}
