package com.pdfgen;

@FunctionalInterface
interface CheckedProcess {

    void run() throws Exception;

}