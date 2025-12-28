package com.pdfgen;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Path;

interface Streams {

    InputStream newInputStream(Path path) throws IOException;

    FileInputStream newFileInputStream(String path) throws FileNotFoundException;

    FileOutputStream newFileOutputStream(String path) throws FileNotFoundException;

    InputStream loadResource(String path);

    String getExternalUrl(String path);

    StandardStreams muteStandardOuts();

    void unmuteStandardOuts(StandardStreams std);
    
}