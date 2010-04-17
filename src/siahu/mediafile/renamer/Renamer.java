package siahu.mediafile.renamer;

import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class Renamer {

	MOVRenamer movRenamer;
	JPGRenamer jpgRenamer;
	
	public Renamer() {
		movRenamer = new MOVRenamer();
		jpgRenamer = new JPGRenamer();
	}
	
	public void rename(File file) {
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
				int b1 = dis.readUnsignedShort();
				if (b1  == 0xFFD8) {
					newName = jpgRenamer.rename(dis);
				} else {
					dis.seek(0);
					newName = movRenamer.rename(dis);
				}
				if (newName != null) {
					System.out.println("Renaming " + file.getName() + " to " + newName);
					file.renameTo(new File(file.getParentFile(), newName));
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
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Renamer ren = new Renamer();
		ren.rename(new File("/media/LaCie/Memories/2009/RENAME/batch04"));
	}

}
