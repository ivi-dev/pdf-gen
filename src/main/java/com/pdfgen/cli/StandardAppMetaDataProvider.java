package com.pdfgen.cli;

import java.io.IOException;
import java.util.jar.Manifest;

class StandardAppMetaDataProvider implements AppMetaDataProvider {

    private static final String MANIFEST_NAME = "META-INF/MANIFEST.MF";

    private static final String PROGRAM_SHORT_NAME_KEY = "Implementation-Short-Name";

    private final Package pkg;

    StandardAppMetaDataProvider() {
        pkg = this.getClass().getPackage();
    }

    @Override
    public String getMetaData() {
        return String.format(
            "%s %s by %s", 
            pkg.getImplementationTitle(), 
            pkg.getImplementationVersion(), 
            pkg.getImplementationVendor()
        );
    }

    @Override
    public String getShortName() {
        var res = getClass().getClassLoader().getResource(MANIFEST_NAME);
        if (res != null) {
            try (var in = res.openStream()) {
                return new Manifest(in).getMainAttributes()
                                       .getValue(PROGRAM_SHORT_NAME_KEY);
            } catch (IOException e) {
                return "n/a";
            }
        }
        return null;
    }

}