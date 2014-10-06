package fi.helsinki.cs.tmc.core.io.zip.zippingdecider;

/**
 * Zips everything.
 */
public class ZipAllTheThings implements ZippingDecider {

    @Override
    public boolean shouldZip(final String zipPath) {

        return true;
    }
}
