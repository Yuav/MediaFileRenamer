package siahu.iso14496.type;

import java.io.DataInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;

import siahu.mov.reader.MOVReader;

/**
 * <pre>
 * Box Type  : ‘stsc’
 * Container : Sample Table Box (‘stbl’)
 * Mandatory : Yes
 * Quantity  : Exactly one
 * </pre>
 * 
 * <p>Samples within the media data are grouped into chunks. Chunks can be of different sizes, and the samples
 * within a chunk can have different sizes. This table can be used to find the chunk that contains a sample, its
 * position, and the associated sample description.
 * 
 * <p>The table is compactly coded. Each entry gives the index of the first chunk of a run of chunks with the same
 * characteristics. By subtracting one entry here from the previous one, you can compute how many chunks are
 * in this run. You can convert this to a sample count by multiplying by the appropriate samples-per-chunk.
 * 
 * <h2>Syntax</h2>
 * <pre>
 * aligned(8) class SampleToChunkBox
 *    extends FullBox(‘stsc’, version = 0, 0) {
 *    unsigned int(32) entry_count;
 *    for (i=1; i <= entry_count; i++) {
 *       unsigned int(32) first_chunk;
 *       unsigned int(32) samples_per_chunk;
 *       unsigned int(32) sample_description_index;
 *    }
 * }
 * </pre>
 * 
 * <h2>Semantics</h2>
 * <p><code>version</code> is an integer that specifies the version of this box
 * <p><code>entry_count</code> is an integer that gives the number of entries in the following table
 * <p><code>first_chunk</code> is an integer that gives the index of the first chunk in this run of chunks that share the
 *    same samples-per-chunk and sample-description-index; the index of the first chunk in a track has the
 *    value 1 (the first_chunk field in the first record of this box has the value 1, identifying that the first
 *    sample maps to the first chunk).
 * <p><code>samples_per_chunk</code> is an integer that gives the number of samples in each of these chunks
 * <p><code>sample_description_index</code> is an integer that gives the index of the sample entry that describes the
 *    samples in this chunk. The index ranges from 1 to the number of sample entries in the Sample
 *    Description Box
 * 
 * @author psiahu
 *
 */
public class StscAtomReader implements AtomReader {

	@Override
	public int read(DataInputStream dis, final int len) throws IOException {

		byte[] buf = new byte[len];
		dis.readFully(buf);
		System.out.println(MOVReader.bytes2hex(buf));
		
		byte version = buf[0];
		System.out.println("Version = " + version);

		System.out.println("Flags = " + buf[1] + buf[2] + buf[3]);
		
		int offset = 4;

		BigInteger entryCount = new BigInteger(Arrays.copyOfRange(buf, offset, offset+=4));
		System.out.println("Entry count = " + entryCount);
		
		for (int i = 0; i < entryCount.intValue(); i++) {
			
			BigInteger firstChunk = new BigInteger(Arrays.copyOfRange(buf, offset, offset+=4));
			System.out.println("Entry " + i + " : First chunk = " + firstChunk);

			BigInteger samplesPerChunk = new BigInteger(Arrays.copyOfRange(buf, offset, offset+=4));
			System.out.println("Entry " + i + " : Samples per chunk = " + samplesPerChunk);
			
			BigInteger samplesDescIdx = new BigInteger(Arrays.copyOfRange(buf, offset, offset+=4));
			System.out.println("Entry " + i + " : Sample description index = " + samplesDescIdx);
		}

		return len;
	}

}
