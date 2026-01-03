package com.pdfgen.cli;

import com.beust.jcommander.ParameterException;

interface AppEngine<T> {

    String getUsage();

    T parseArgs(String[] args) throws ParameterException;
    
}
