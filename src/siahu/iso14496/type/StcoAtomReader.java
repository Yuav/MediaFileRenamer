package siahu.iso14496.type;

import java.io.DataInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <h2>Definition</h2>
 * 
 * <pre>
 * Box Type  : ‘stco’, ‘co64’
 * Container : Sample Table Box (‘stbl’)
 * Mandatory : Yes
 * Quantity  : Exactly one variant must be present
 * </pre>
 * 
 * <p>
 * The chunk offset table gives the index of each chunk into the containing
 * file. There are two variants, permitting the use of 32-bit or 64-bit offsets.
 * The latter is useful when managing very large presentations. At most one of
 * these variants will occur in any single instance of a sample table.
 * 
 * <p>
 * Offsets are file offsets, not the offset into any box within the file (e.g.
 * Media Data Box). This permits referring to media data in files without any
 * box structure. It does also mean that care must be taken when constructing a
 * self-contained ISO file with its metadata (Movie Box) at the front, as the
 * size of the Movie Box will affect the chunk offsets to the media data.
 * 
 * <h2>Syntax</h2>
 * 
 * <pre>
 * aligned(8) class ChunkOffsetBox
 *    extends FullBox(‘stco’, version = 0, 0) {
 *    unsigned int(32) entry_count;
 *    for (i=1; i <= entry_count; i++) {
 *       unsigned int(32) chunk_offset;
 *    }
 * }
 * aligned(8) class ChunkLargeOffsetBox
 *    extends FullBox(‘co64’, version = 0, 0) {
 *    unsigned int(32) entry_count;
 *    for (i=1; i <= entry_count; i++) {
 *       unsigned int(64) chunk_offset;
 *    }
 * }
 * </pre>
 * 
 * @author psiahu
 * 
 */
public class StcoAtomReader implements AtomReader {

    private Logger logger;
    
    public StcoAtomReader() {
        logger = Logger.getLogger(this.getClass().getName());
    }

    @Override
    public int read(DataInputStream dis, final int len) throws IOException {

        byte[] buf = new byte[len];
        dis.readFully(buf);

        // version is an integer that specifies the version of this box
        byte version = buf[0];
        if (logger.isLoggable(Level.FINEST)) {
            logger.finest("Version = " + version);
            logger.finest("Flags = " + buf[1] + buf[2] + buf[3]);
        }

        int offset = 4;

        // entry_count is an integer that gives the number of entries in the
        // following table
        BigInteger entryCount = new BigInteger(Arrays.copyOfRange(buf, offset,
                offset += 4));
        if (logger.isLoggable(Level.FINEST)) {
            logger.finest("Entry count = " + entryCount);
        }

        // chunk_offset is a 32 or 64 bit integer that gives the offset of the
        // start of a chunk into its containing
        // media file.
        for (int i = 0; i < entryCount.intValue(); i++) {
            // BigInteger chunkOffset = new BigInteger(Arrays.copyOfRange(buf,
            // offset, offset += 4));
        }

        return len;
    }

}
