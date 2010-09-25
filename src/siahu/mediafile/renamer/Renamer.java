package siahu.mediafile.renamer;

import java.io.File;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;

public class Renamer {

    private Logger logger;

    public Renamer() {
        logger = Logger.getLogger(this.getClass().getName());
    }

    public void rename(Set<RenameItem> list) {
        Iterator<RenameItem> it = list.iterator();
        while (it.hasNext()) {
            RenameItem item = it.next();
            File file = item.getFile();
            String newName = item.getNewName();
            logger.info("Renaming " + file.getName() + " to " + newName);
            file.renameTo(new File(file.getParentFile(), newName));
        }
    }

}
