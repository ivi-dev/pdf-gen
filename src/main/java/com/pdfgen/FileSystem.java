package com.pdfgen;

import java.nio.file.Path;

interface FileSystem {

    Path getPath(String filename);

    boolean isDirectory(Path path);
    
}