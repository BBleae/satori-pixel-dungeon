package studio.baka.satoripixeldungeon.journal;

import studio.baka.satoripixeldungeon.SatoriPixelDungeon;
import com.watabou.utils.Bundle;
import com.watabou.utils.FileUtils;

import java.io.IOException;

public class Journal {

    public static final String JOURNAL_FILE = "journal.dat";

    private static boolean loaded = false;

    public static void loadGlobal() {
        if (loaded) {
            return;
        }

        Bundle bundle;
        try {
            bundle = FileUtils.bundleFromFile(JOURNAL_FILE);

        } catch (IOException e) {
            bundle = new Bundle();
        }

        Catalog.restore(bundle);
        Document.restore(bundle);

        loaded = true;
    }

    //package-private
    static boolean saveNeeded = false;

    public static void saveGlobal() {
        if (!saveNeeded) {
            return;
        }

        Bundle bundle = new Bundle();

        Catalog.store(bundle);
        Document.store(bundle);

        try {
            FileUtils.bundleToFile(JOURNAL_FILE, bundle);
            saveNeeded = false;
        } catch (IOException e) {
            SatoriPixelDungeon.reportException(e);
        }

    }

}
