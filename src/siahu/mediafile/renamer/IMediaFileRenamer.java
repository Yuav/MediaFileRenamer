package siahu.mediafile.renamer;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public interface IMediaFileRenamer {

    /**
     * Examines the given File and returns whether or not it can handle it.
     * 
     * @param raf
     *            The random access file instance of the file to be checked for
     *            its type. For some formats, the type can be determined by
     *            reading the content.
     * @param file
     *            The media file to be checked for its type. Some formats do not
     *            magic numbers, so the type can be inferred from the extension
     *            of the filename.
     * @return true if this instance can handle
     * @throws IOException
     */
    public boolean canHandle(RandomAccessFile raf, File file)
            throws IOException;

    /**
     * Renames the given file
     * 
     * @param raf
     *            The random access file instance of the file to be renamed.
     *            Passing this instead of instantiating it in the method helps
     *            with the overhead associated with opening and closing the file
     *            here. Especially since canHandle() also operates on the same
     *            type.
     * @param file
     *            The media file to be renamed. AVI file format does not have
     *            date metadata, so the date has to be retrieved from
     *            File.lastModified()
     * @return The new name
     * @throws IOException
     */
    public String rename(RandomAccessFile raf, File file) throws IOException;
}
