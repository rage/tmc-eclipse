package fi.helsinki.cs.tmc.core.old.old.io.zip.unzippingdecider;

/**
 * Unzips everything.
 */
public class UnzipAllTheThings implements UnzippingDecider {

    @Override
    public boolean shouldUnzip(final String filePath) {

        return true;
    }
}
