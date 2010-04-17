package siahu.iso14496.type;

import java.io.DataInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;

import siahu.mov.reader.MOVReader;

/**
 * <h2>Definition</h2>
 * <pre>
 * Box Type  : ‘mdhd’
 * Container : Media Box (‘mdia’)
 * Mandatory : Yes
 * Quantity  : Exactly one
 * </pre>
 * 
 * <p>The media header declares overall information that is media-independent, and relevant to characteristics of
 * the media in a track.
 *
 * <h2>Syntax</h2>
 * <pre>
 * aligned(8) class MediaHeaderBox extends FullBox(‘mdhd’, version, 0) {
 *    if (version==1) {
 *       unsigned int(64) creation_time;
 *       unsigned int(64) modification_time;
 *       unsigned int(32) timescale;
 *       unsigned int(64) duration;
 *    } else { // version==0
 *       unsigned int(32) creation_time;
 *       unsigned int(32) modification_time;
 *       unsigned int(32) timescale;
 *       unsigned int(32) duration;
 *    }
 *    bit(1)   pad = 0;
 *    unsigned int(5)[3]   language;   // ISO-639-2/T language code
 *    unsigned int(16) pre_defined = 0;
 * }
 * </pre>
 * 
 * <h2>Semantics</h2>
 * <p><code>version</code> is an integer that specifies the version of this box (0 or 1)
 * <p><code>creation_time</code> is an integer that declares the creation time of the media in this track (in seconds since
 *    midnight, Jan. 1, 1904, in UTC time)
 * <p><code>modification_time</code> is an integer that declares the most recent time the media in this track was
 *    modified (in seconds since midnight, Jan. 1, 1904, in UTC time)
 * <p><code>timescale</code> is an integer that specifies the time-scale for this media; this is the number of time units that
 *    pass in one second. For example, a time coordinate system that measures time in sixtieths of a
 *    second has a time scale of 60.
 * <p><code>duration</code> is an integer that declares the duration of this media (in the scale of the timescale).
 * <p><code>language</code> declares the language code for this media. See ISO 639-2/T for the set of three character
 *    codes. Each character is packed as the difference between its ASCII value and 0x60. Since the code
 *    is confined to being three lower-case letters, these values are strictly positive.
 * 
 * @author psiahu
 *
 */
public class MdhdAtomReader implements AtomReader {

	@Override
	public int read(DataInputStream dis, final int len) throws IOException {

		byte[] buf = new byte[len];
		dis.readFully(buf);
		System.out.println(MOVReader.bytes2hex(buf));
		
		byte version = buf[0];
		System.out.println("Version = " + version);

		System.out.println("Flags = " + buf[1] + buf[2] + buf[3]);

		int offset = 4;

		BigInteger creationTime = null;
		if (version == 0x00) {
			creationTime = new BigInteger(Arrays.copyOfRange(buf, offset, offset+=4));
		} else if (version == 0x01) {
			creationTime = new BigInteger(Arrays.copyOfRange(buf, offset, offset+=8));
		}
		System.out.println("Creation time = " + MOVReader.formatDate(creationTime.longValue()));
		
		BigInteger modificationTime = null;
		if (version == 0x00) {
			modificationTime = new BigInteger(Arrays.copyOfRange(buf, offset, offset+=4));
		} else if (version == 0x01) {
			modificationTime = new BigInteger(Arrays.copyOfRange(buf, offset, offset+=8));
		}
		System.out.println("Modification time = " + MOVReader.formatDate(modificationTime.longValue()));

		BigInteger timescale = new BigInteger(Arrays.copyOfRange(buf, offset, offset+=4));
		System.out.println("Timescale = " + timescale);
		
		BigInteger duration = null;
		if (version == 0) {
			duration = new BigInteger(Arrays.copyOfRange(buf, offset, offset+=4));
		} else if (version == 1) {
			duration = new BigInteger(Arrays.copyOfRange(buf, offset, offset+=8));
		}
		System.out.println("Duration = " + duration);

		offset += 2; // language

		offset += 2; // reserved

		return len;
	}

}
