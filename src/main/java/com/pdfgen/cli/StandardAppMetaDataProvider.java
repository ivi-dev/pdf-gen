package com.pdfgen.cli;

class StandardAppMetaDataProvider implements AppMetaDataProvider {

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

}