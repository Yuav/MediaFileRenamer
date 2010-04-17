package siahu.media.builtin;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import siahu.media.base.MediaIdentifier;
import siahu.media.base.MediaReader;

public class MOVMediaIdentifier implements MediaIdentifier {

	@Override
	public MediaReader getReader(File media) throws IOException {
		RandomAccessFile raf = null;
		MediaReader reader = null;
		try {
			raf = new RandomAccessFile(media, "r");
			raf.seek(4);
			int type = raf.readInt();
			if (type == 0x66747970) {
				System.out.println("Found MOV type");
				reader = new MOVMediaReader();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (raf != null) {
				raf.close();
			}
		}
		return reader;
	}

}
