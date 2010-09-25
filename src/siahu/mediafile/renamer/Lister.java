package siahu.mediafile.renamer;

import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

public class Lister {

    private File[] files;
    private ArrayList<IMediaFileRenamer> plugins;
    private Set<RenameItem> renameList;

    /**
     * Constructor
     * 
     * @param files
     *            Array of File objects to be renamed
     */
    public Lister(File[] files) {
        this.files = files;
        this.plugins = new ArrayList<IMediaFileRenamer>();
        plugins.add(new MOVRenamer());
        plugins.add(new JPGRenamer());
        this.renameList = new TreeSet<RenameItem>();
    }

    /**
     * Lists new names and potential errors
     */
    public void list() {
        for (int i = 0; i < files.length; i++) {
            rename(files[i]);
        }
        Iterator<RenameItem> it = this.renameList.iterator();
        while (it.hasNext()) {
            RenameItem ri = it.next();
            System.out.println("Renaming " + ri.getFile().getName() + " to "
                    + ri.getNewName());
        }
    }

    /**
     * The RenameList is populated in the rename(File) method.
     * 
     * @return Rename List
     */
    public Set<RenameItem> getRenameList() {
        return renameList;
    }

    private void rename(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                rename(files[i]);
            }
        } else if (file.isFile()) {
            RandomAccessFile dis = null;
            try {
                dis = new RandomAccessFile(file, "r");
                String newName = null;
                for (int i = 0; i < plugins.size(); i++) {
                    IMediaFileRenamer plugin = plugins.get(i);
                    if (plugin.canHandle(dis)) {
                        newName = plugin.rename(dis);
                        break;
                    }
                }
                if (newName != null) {
                    if (newName.equals(file.getName()) == false) {
                        // System.out.println("Renaming " + file.getName() +
                        // " to " + newName);
                        RenameItem renameItem = new RenameItem(file, newName);
                        this.renameList.add(renameItem);
                    }
                } else {
                    System.err.println("Cannot rename " + file.getName());
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (EOFException e) {
                // System.out.println("EOF");
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (dis != null) {
                    try {
                        dis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}
