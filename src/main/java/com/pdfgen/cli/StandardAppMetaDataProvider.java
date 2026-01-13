package com.pdfgen.cli;

class StandardAppMetaDataProvider implements AppMetaDataProvider {

    @Override
    public String getMetaData() {
        var pkg = this.getClass().getPackage();
        var title = pkg.getImplementationTitle();
        var version = pkg.getImplementationVersion();
        var vendor = pkg.getImplementationVendor();
        return String.format("%s %s by %s", title, version, vendor);
    }

}