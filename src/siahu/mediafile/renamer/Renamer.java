package siahu.mediafile.renamer;

import java.io.File;
import java.util.Iterator;
import java.util.List;

public class Renamer {

	public Renamer() {
	}

	public void rename(List<RenameItem> list) {
		Iterator<RenameItem> it = list.iterator();
		while (it.hasNext()) {
			RenameItem item = it.next();
			File file = item.getFile();
			String newName = item.getNewName();
			System.out.println("Renaming " + file.getName() + " to " + newName);
			file.renameTo(new File(file.getParentFile(), newName));
		}
	}

}
