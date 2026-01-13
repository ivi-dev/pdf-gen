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
            getTitle(), 
            getVersion(), 
            getMeintainer()
        );
    }

    @Override
    public String getTitle() {
        return pkg.getImplementationTitle();
    }

    @Override
    public String getVersion() {
        return pkg.getImplementationVersion();
    }

    @Override
    public String getMeintainer() {
        return pkg.getImplementationVendor();
    }

}