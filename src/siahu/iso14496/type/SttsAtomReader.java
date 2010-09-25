package siahu.iso14496.type;

import java.io.DataInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;

import siahu.mov.reader.MOVReader;

/**
 * <h2>Definition</h2>
 * 
 * <pre>
 * Box Type  : ‘stts’
 * Container : Sample Table Box (‘stbl’)
 * Mandatory : Yes
 * Quantity  : Exactly one
 * </pre>
 * 
 * <p>
 * This box contains a compact version of a table that allows indexing from
 * decoding time to sample number. Other tables give sample sizes and pointers,
 * from the sample number. Each entry in the table gives the number of
 * consecutive samples with the same time delta, and the delta of those samples.
 * By adding the deltas a complete time-to-sample map may be built.
 * 
 * <p>
 * The Decoding Time to Sample Box contains decode time delta's: DT(n+1) = DT(n)
 * + STTS(n) where STTS(n) is the (uncompressed) table entry for sample n.
 * 
 * <p>
 * The sample entries are ordered by decoding time stamps; therefore the deltas
 * are all non-negative.
 * 
 * <p>
 * The DT axis has a zero origin; DT(i) = SUM(for j=0 to i-1 of delta(j)), and
 * the sum of all deltas gives the length of the media in the track (not mapped
 * to the overall timescale, and not considering any edit list).
 * 
 * <p>
 * The Edit List Box provides the initial CT value if it is non-empty
 * (non-zero).
 * 
 * <h2>Syntax</h2>
 * 
 * <pre>
 * aligned(8) class TimeToSampleBox
 *    extends FullBox(’stts’, version = 0, 0) {
 *    unsigned int(32) entry_count;
 *       int i;
 *    for (i=0; i < entry_count; i++) {
 *       unsigned int(32) sample_count;
 *       unsigned int(32) sample_delta;
 *    }
 * }
 * </pre>
 * 
 * <h2>Semantics</h2>
 * <p>
 * <code>version</code> - is an integer that specifies the version of this box.
 * <p>
 * <code>entry_count</code> - is an integer that gives the number of entries in
 * the following table.
 * <p>
 * <code>sample_count</code> - is an integer that counts the number of
 * consecutive samples that have the given duration.
 * <p>
 * <code>sample_delta</code> - is an integer that gives the delta of these
 * samples in the time-scale of the media.
 * 
 * @author psiahu
 * 
 */
public class SttsAtomReader implements AtomReader {

    @Override
    public int read(DataInputStream dis, final int len) throws IOException {

        byte[] buf = new byte[len];
        dis.readFully(buf);
        System.out.println(MOVReader.bytes2hex(buf));

        byte version = buf[0];
        System.out.println("Version = " + version);

        System.out.println("Flags = " + buf[1] + buf[2] + buf[3]);

        int offset = 4;

        BigInteger entryCount = new BigInteger(Arrays.copyOfRange(buf, offset,
                offset += 4));
        System.out.println("Entry count = " + entryCount);

        for (int i = 0; i < entryCount.intValue(); i++) {

            BigInteger sampleCount = new BigInteger(Arrays.copyOfRange(buf,
                    offset, offset += 4));
            System.out.println("Entry " + i + " : Sample count = "
                    + sampleCount);

            BigInteger sampleDelta = new BigInteger(Arrays.copyOfRange(buf,
                    offset, offset += 4));
            System.out.println("Entry " + i + " : Sample delta = "
                    + sampleDelta);
        }

        return len;
    }

}
