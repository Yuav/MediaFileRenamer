package siahu.mediafile.renamer;

import java.io.IOException;
import java.io.RandomAccessFile;

public interface IMediaFileRenamer {

    /**
     * Examines the given File and returns whether or not it can handle it.
     * 
     * @param file
     *            The media file to be renamed
     * @return true if this instance can handle
     * @throws IOException
     */
    public boolean canHandle(RandomAccessFile file) throws IOException;

    /**
     * Renames the given file
     * 
     * @param file
     *            The media file to be renamed
     * @return The new name
     * @throws IOException
     */
    public String rename(RandomAccessFile file) throws IOException;
}
