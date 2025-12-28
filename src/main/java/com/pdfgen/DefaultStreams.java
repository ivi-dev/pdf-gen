package com.pdfgen;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

class DefaultStreams implements Streams {

    @Override
    public InputStream newInputStream(Path path) throws IOException {
        return Files.newInputStream(path);
    }

    @Override
    public FileInputStream newFileInputStream(String path) throws FileNotFoundException {
        return new FileInputStream(path);
    }

    @Override
    public FileOutputStream newFileOutputStream(String path) throws FileNotFoundException {
        return new FileOutputStream(path);
    }

    @Override
    public InputStream loadResource(String path) {
        return getClass().getResourceAsStream(path);
    }

    @Override
    public String getExternalUrl(String path) {
        var resource = getClass().getResource("/" + path);
        return resource != null ? resource.toExternalForm() : null;
    }

    @Override
    public StandardStreams muteStandardOuts() {
        PrintStream out = System.out; 
        PrintStream err = System.err; 
        System.setOut(new PrintStream(OutputStream.nullOutputStream())); 
        System.setErr(new PrintStream(OutputStream.nullOutputStream()));
        return new StandardStreams(out, err);
    }

    @Override
    public void unmuteStandardOuts(StandardStreams std) {
        System.setOut(std.out()); 
        System.setErr(std.err());
    }

}