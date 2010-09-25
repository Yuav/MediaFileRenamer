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
import java.util.logging.Level;
import java.util.logging.Logger;

public class Lister {

    private File[] files;
    private ArrayList<IMediaFileRenamer> plugins;
    private Set<RenameItem> renameList;
    private Logger logger;

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
        plugins.add(new AVIRenamer());
        this.renameList = new TreeSet<RenameItem>();
        logger = Logger.getLogger(this.getClass().getName());
    }

    /**
     * Lists new names and potential errors
     */
    public void list() {
        for (int i = 0; i < files.length; i++) {
            if (logger.isLoggable(Level.FINE)) {
                logger.fine("Processing " + files[i]);
            }
            rename(files[i]);
        }
        Iterator<RenameItem> it = this.renameList.iterator();
        while (it.hasNext()) {
            RenameItem ri = it.next();
            logger.info("Renaming " + ri.getFile().getName() + " to "
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
                for(int i=0; i<plugins.size(); i++) {
                    IMediaFileRenamer plugin = plugins.get(i);
                    if (plugin.canHandle(dis, file)) {
                        newName = plugin.rename(dis, file);
                        if (i > 0) {
                            plugins.remove(i);
                            plugins.add(0, plugin);
                        }
                        break;
                    }
                }
                if (newName != null) {
                    if (newName.equals(file.getName()) == false) {
                        if (logger.isLoggable(Level.FINE)) {
                            logger.fine("Renaming " + file.getName() + " to "
                                    + newName);
                        }
                        RenameItem renameItem = new RenameItem(file, newName);
                        this.renameList.add(renameItem);
                    }
                } else {
                    logger.warning("Cannot rename " + file.getName());
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
