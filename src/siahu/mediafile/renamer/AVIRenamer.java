package siahu.mediafile.renamer;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AVIRenamer implements IMediaFileRenamer {

    private SimpleDateFormat toSdf;
    private Logger logger;

    public AVIRenamer() {
        toSdf = new SimpleDateFormat("yyyyMMdd-HHmmss");
        logger = Logger.getLogger(this.getClass().getName());
    }

    @Override
    public boolean canHandle(RandomAccessFile raf, File file) throws IOException {
        boolean canHandle = false;
        
        raf.seek(0);
        byte[] riffHeader = new byte[4];
        raf.readFully(riffHeader);
        if (new String(riffHeader).equals("RIFF")) {
            raf.skipBytes(4);
            byte[] aviType = new byte[4];
            raf.readFully(aviType);
            if (new String(aviType).equals("AVI ")) {
                canHandle = true;
            }
        }
        if (canHandle) {
            if (logger.isLoggable(Level.FINE)) {
                logger.fine("Can handle " + raf);
            }
        } else {
            if (logger.isLoggable(Level.FINER)) {
                logger.finer("Cannot handle " + raf);
            }
        }
        return canHandle;
    }

    @Override
    public String rename(RandomAccessFile raf, File file) throws IOException {
        Date d = new Date(file.lastModified());
        return toSdf.format(d)  + ".AVI";
    }

}
