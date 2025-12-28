package com.pdfgen;

import java.nio.file.Files;
import java.nio.file.Path;

class DefaultFileSystem implements FileSystem {

    @Override
    public Path getPath(String filename) {
        return Path.of(filename);
    }

    @Override
    public boolean isDirectory(Path path) {
        return Files.isDirectory(path);
    }

}