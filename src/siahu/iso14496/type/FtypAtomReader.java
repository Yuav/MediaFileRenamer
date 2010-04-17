package siahu.iso14496.type;

import java.io.DataInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;

import siahu.mov.reader.MOVReader;

/**
 * <h2>Definition</h2>
 * <pre>
 * Box Type  : ‘ftyp’
 * Container : File
 * Mandatory : Yes
 * Quantity  : Exactly one
 * </pre>
 * 
 * <p>Files written to this version of this specification must contain a file-type box. For compatibility with an earlier
 * version of this specification, files may be conformant to this specification and not contain a file-type box. Files
 * with no file-type box should be read as if they contained an FTYP box with <code>Major_brand='mp41',
 * minor_version=0</code>, and the single compatible brand <code>'mp41'</code>.
 * 
 * <p>A media-file structured to this part of this specification may be compatible with more than one detailed
 * specification, and it is therefore not always possible to speak of a single ‘type’ or ‘brand’ for the file. This
 * means that the utility of the file name extension and Multipurpose Internet Mail Extension (MIME) type are
 * somewhat reduced.
 * 
 * <p>This box must be placed as early as possible in the file (e.g. after any obligatory signature, but before any
 * significant variable-size boxes such as a Movie Box, Media Data Box, or Free Space). It identifies which
 * specification is the ‘best use’ of the file, and a minor version of that specification; and also a set of other
 * specifications to which the file complies. Readers implementing this format should attempt to read files that
 * are marked as compatible with any of the specifications that the reader implements. Any incompatible change
 * in a specification should therefore register a new ‘brand’ identifier to identify files conformant to the new
 * specification.
 * 
 * <p>The minor version is informative only. It does not appear for compatible-brands, and must not be used to
 * determine the conformance of a file to a standard. It may allow more precise identification of the major
 * specification, for inspection, debugging, or improved decoding.
 * 
 * <p>Files would normally be externally identified (e.g. with a file extension or mime type) that identifies the ‘best
 * use’ (major brand), or the brand that the author believes will provide the greatest compatibility.
 *
 * <h2>Syntax</h2>
 * <pre>
 * aligned(8) class FileTypeBox
 *    extends Box(‘ftyp’) {
 *    unsigned int(32) major_brand;
 *    unsigned int(32) minor_version;
 *    unsigned int(32) compatible_brands[];
 * }
 * </pre>
 * 
 * <h2>Semantics</h2>
 * <p>This box identifies the specifications to which this file complies.
 * <p>Each brand is a printable four-character code, registered with ISO, that identifies a precise specification.
 * <p><code>major_brand</code> – is a brand identifier
 * <p><code>minor_version</code> – is an informative integer for the minor version of the major brand
 * <p><code>compatible_brands</code> – is a list, to the end of the box, of brands
 * 
 * @author psiahu
 *
 */
public class FtypAtomReader extends BaseAtomReader {

	public FtypAtomReader() {
	}
	
	@Override
	public int read(DataInputStream dis, final int len) throws IOException {
		
		byte[] buf = new byte[len];
		dis.readFully(buf);
		System.out.println(MOVReader.bytes2hex(buf));

		int offset = 0;
		
		String majorBrand = new String(Arrays.copyOfRange(buf, offset, offset+=4));
		System.out.println("Major brand = " + majorBrand);

		BigInteger minorVersion = new BigInteger(Arrays.copyOfRange(buf, offset, offset+=4));
		System.out.println("Minor version = " + minorVersion);
		
		while (offset < len) {
			String compatibleBrand = new String(Arrays.copyOfRange(buf, offset, offset+=4));
			System.out.println("Compatible brand = " + compatibleBrand);
		}

		return len;
	}

}
