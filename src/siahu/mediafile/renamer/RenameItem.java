package siahu.mediafile.renamer;

import java.io.File;

public class RenameItem implements Comparable<RenameItem> {

    private File file;
    private String newName;

    public RenameItem(File file, String newName) {
        this.file = file;
        this.newName = newName;
    }

    public File getFile() {
        return file;
    }

    public String getNewName() {
        return newName;
    }

    @Override
    public int compareTo(RenameItem o) {
        if (o != null) {
            return file.getName().compareTo(o.getFile().getName());
        }
        return 1;
    }

}
