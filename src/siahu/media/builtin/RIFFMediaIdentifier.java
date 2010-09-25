package siahu.media.builtin;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import siahu.media.base.MediaIdentifier;
import siahu.media.base.MediaReader;

public class RIFFMediaIdentifier implements MediaIdentifier {

    @Override
    public MediaReader getReader(File media) throws IOException {
        MediaReader reader = null;
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(media, "r");
            int type = raf.readInt();
            if (type == 0x52494646) {
                System.out.println("Found RIFF type");
                reader = new RIFFMediaReader();
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
