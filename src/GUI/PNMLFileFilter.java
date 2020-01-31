package GUI;

import java.io.File;
import java.io.FileFilter;

public class PNMLFileFilter implements FileFilter {
    @Override
    public boolean accept(File pathname) {
        String suffix = ".pnml";

        if (pathname.getName().toLowerCase().endsWith(suffix))
            return true;

        return false;
    }
}
