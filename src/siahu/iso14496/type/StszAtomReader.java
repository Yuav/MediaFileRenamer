package siahu.iso14496.type;

import java.io.DataInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;

import siahu.mov.reader.MOVReader;

/**
 * <pre>
 * Box Type  : ‘stsz’, ‘stz2’
 * Container : Sample Table Box (‘stbl’)
 * Mandatory : Yes
 * Quantity  : Exactly one variant must be present
 * </pre>
 * 
 * <p>
 * This box contains the sample count and a table giving the size in bytes of
 * each sample. This allows the media data itself to be unframed. The total
 * number of samples in the media is always indicated in the sample count.
 * 
 * <p>
 * There are two variants of the sample size box. The first variant has a fixed
 * size 32-bit field for representing the sample sizes; it permits defining a
 * constant size for all samples in a track. The second variant permits smaller
 * size fields, to save space when the sizes are varying but small. One of these
 * boxes must be present; the first version is preferred for maximum
 * compatibility.
 * 
 * <h2>Syntax</h2>
 * 
 * <pre>
 * aligned(8) class SampleSizeBox extends FullBox(‘stsz’, version = 0, 0) {
 *    unsigned int(32) sample_size;
 *    unsigned int(32) sample_count;
 *    if (sample_size==0) {
 *       for (i=1; i <= sample_count; i++) {
 *       unsigned int(32) entry_size;
 *       }
 *    }
 * }
 * </pre>
 * 
 * <h2>Semantics</h2>
 * <p>
 * <code>version</code> is an integer that specifies the version of this box
 * <p>
 * <code>sample_size</code> is integer specifying the default sample size. If
 * all the samples are the same size, this field contains that size value. If
 * this field is set to 0, then the samples have different sizes, and those
 * sizes are stored in the sample size table. If this field is not 0, it
 * specifies the constant sample size, and no array follows.
 * <p>
 * <code>sample_count</code> is an integer that gives the number of samples in
 * the track; if sample-size is 0, then it is also the number of entries in the
 * following table.
 * 
 * @author psiahu
 * 
 */
public class StszAtomReader implements AtomReader {

    @Override
    public int read(DataInputStream dis, final int len) throws IOException {

        byte[] buf = new byte[len];
        dis.readFully(buf);
        // System.out.println(MOVReader.bytes2hex(buf));

        byte version = buf[0];
        System.out.println("Version = " + version);

        System.out.println("Flags = " + buf[1] + buf[2] + buf[3]);

        int offset = 4;

        BigInteger samplesize = new BigInteger(Arrays.copyOfRange(buf, offset,
                offset += 4));
        System.out.println("Sample Size = " + samplesize);

        BigInteger sampleCount = new BigInteger(Arrays.copyOfRange(buf, offset,
                offset += 4));
        System.out.println("Sample count = " + sampleCount);

        return len;
    }

}
