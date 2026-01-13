package com.pdfgen.cli;

import com.beust.jcommander.ParameterException;

interface AppEngine<T> {

    String getUsage();

    String getMetaData();

    T parseArgs(String[] args) throws ParameterException;
    
}
