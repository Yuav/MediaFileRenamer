package siahu.mediafile.renamer;

import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

public class Lister {

	private File[] files;
	private ArrayList<IMediaFileRenamer> plugins;
	
	/**
	 * Constructor
	 * 
	 * @param files Array of File objects to be renamed
	 */
	public Lister(File[] files) {
		this.files = files;
		this.plugins = new ArrayList<IMediaFileRenamer>();
		plugins.add(new MOVRenamer());
		plugins.add(new JPGRenamer());
	}
	
	/**
	 * Lists new names and potential errors
	 */
	public void list() {
		for (int i = 0; i < files.length; i++) {
			rename(files[i]);
		}
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
					System.out.println("Renaming " + file.getName() + " to " + newName);
//					file.renameTo(new File(file.getParentFile(), newName));
				} else {
					System.err.println("Cannot rename " + file.getName());
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (EOFException e) {
//				System.out.println("EOF");
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
