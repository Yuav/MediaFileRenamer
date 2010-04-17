package siahu.iso14496.type;

import java.io.DataInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;

import siahu.mov.reader.MOVReader;

/**
 * <pre>
 * Box Types : ‘stsd’
 * Container : Sample Table Box (‘stbl’)
 * Mandatory : Yes
 * Quantity  : Exactly one
 * </pre>
 * <p>The sample description table gives detailed information about the coding type used, and any initialization
 * information needed for that coding.
 * <p>The information stored in the sample description box after the entry-count is both track-type specific as
 * documented here, and can also have variants within a track type (e.g. different codings may use different
 * specific information after some common fields, even within a video track).
 * <p>For video tracks, a VisualSampleEntry is used, for audio tracks, an AudioSampleEntry and for metadata
 * tracks, a MetaDataSampleEntry. Hint tracks use an entry format specific to their protocol, with an appropriate
 * name.
 * <p>For hint tracks, the sample description contains appropriate declarative data for the streaming protocol being
 * used, and the format of the hint track. The definition of the sample description is specific to the protocol.
 * <p>Multiple descriptions may be used within a track.
 * <p>The ‘protocol’ and ‘codingname’ fields are registered identifiers that uniquely identify the streaming protocol or
 * compression format decoder to be used. A given protocol or codingname may have optional or required
 * extensions to the sample description (e.g. codec initialization parameters). All such extensions shall be within
 * boxes; these boxes occur after the required fields. Unrecognized boxes shall be ignored.
 * <p>If the ‘format’ field of a SampleEntry is unrecognized, neither the sample description itself, nor the associated
 * media samples, shall be decoded.
 * <p>The samplerate, samplesize and channelcount fields document the default audio output playback format for
 * this media. The timescale for an audio track should be chosen to match the sampling rate, or be an integer
 * multiple of it, to enable sample-accurate timing.
 * <p>In video tracks, the frame_count field must be 1 unless the specification for the media format explicitly
 * documents this template field and permits larger values. That specification must document both how the
 * individual frames of video are found (their size information) and their timing established. That timing might be
 * as simple as dividing the sample duration by the frame count to establish the frame duration.
 * <p>       NOTE : though the count is 32 bits, the number of items is usually much fewer, and is restricted by the fact
 *           that the reference index in the sample table is only 16 bits
 * <p>An optional BitRateBox may be present at the end of any MetaDataSampleEntry to signal the bit rate
 * information of a stream. This can be used for buffer configuration. In case of XML metadata it can be used to
 * choose the appropriate memory representation format (DOM, STX).
 * <p>The width and height in the video sample entry document the pixel counts that the codec will deliver; this
 * enables the allocation of buffers. Since these are counts they do not take into account pixel aspect ratio.
 * <p>The pixel aspect ratio and clean aperture of the video may be specified using the ‘pasp’ and ‘clap’
 * sample entry boxes, respectively. These are both optional; if present, they over-ride the declarations (if any) in
 * structures specific to the video codec, which structures should be examined if these boxes are absent.
 * <p>In the PixelAspectRatioBox, hSpacing and vSpacing have the same units, but those units are unspecified:
 * only the ratio matters. hSpacing and vSpacing may or may not be in reduced terms, and they may reduce
 * to 1/1. Both of them must be positive.
 * <p>They are defined as the aspect ratio of a pixel, in arbitrary units. If a pixel appears H wide and V tall, then
 * hSpacing/vSpacing is equal to H/V. This means that a square on the display that is n pixels tall needs to be
 * n*vSpacing/hSpacing pixels wide to appear square.
 * <p>         NOTE : When adjusting pixel aspect ratio, normally, the horizontal dimension of the video is scaled, if needed
 *          (i.e. if the final display system has a different pixel aspect ratio from the video source).
 * <p>         NOTE : It is recommended that the original pixels, and the composed transform, be carried through the
 *          pipeline as far as possible. If the transformation resulting from ‘correcting’ pixel aspect ratio to a square grid,
 *          normalizing to the track dimensions, composition or placement (e.g. track and/or movie matrix), and normalizing
 *          to the display characteristics, is a unity matrix, then no re-sampling need be done. In particular, video should not
 *          be re-sampled more than once in the process of rendering, if at all possible.
 * <p>There are notionally four values in the CleanApertureBox. These parameters are represented as a fraction
 * N/D. The fraction may or may not be in reduced terms. We refer to the pair of parameters fooN and fooD as
 * foo. For horizOff and vertOff, D must be positive and N may be positive or negative. For
 * cleanApertureWidth and cleanApertureHeight, both N and D must be positive.
 * <p>         NOTE : These are fractional numbers for several reasons. First, in some systems the exact width after pixel
 *          aspect ratio correction is integral, not the pixel count before that correction. Second, if video is resized in the full
 *          aperture, the exact expression for the clean aperture may not be integral. Finally, because this is represented
 *          using centre and offset, a division by two is needed, and so half-values can occur.
 * <p>Considering the pixel dimensions as defined by the VisualSampleEntry width and height. If picture centre of
 * the image is at pcX and pcY, then horizOff and vertOff are defined as follows:
 * <pre>
 *        pcX = horizOff + (width - 1)/2
 *        pcY = vertOff + (height - 1)/2;
 * </pre>
 * <p>Typically, horizOff and vertOff are zero, so the image is centred about the picture centre.
 * <p>The leftmost/rightmost pixel and the topmost/bottommost line of the clean aperture fall at:
 * <pre>
 *        pcX ± (cleanApertureWidth - 1)/2
 *        pcY ± (cleanApertureHeight - 1)/2;
 * </pre>
 * <p>The audio output format (samplerate, samplesize and channelcount fields) in the sample entry should be
 * considered definitive only for codecs that do not record their own output configuration. If the audio codec has
 * definitive information about the output format, it shall be taken as definitive; in this case the samplerate,
 * samplesize and channelcount fields in the sample entry may be ignored, though sensible values should be
 * chosen (for example, the highest possible sampling rate).
 *
 * @author psiahu
 *
 */
public class StsdAtomReader implements AtomReader {

	@Override
	public int read(DataInputStream dis, final int len) throws IOException {

		byte[] buf = new byte[len];
		dis.readFully(buf);
		System.out.println(MOVReader.bytes2hex(buf));

		// version is an integer that specifies the version of this box
		byte version = buf[0];
		System.out.println("Version = " + version);

		System.out.println("Flags = " + buf[1] + buf[2] + buf[3]);

		int offset = 4;
		
		// entry_count is an integer that gives the number of entries in the following table
		BigInteger entryCount = new BigInteger(Arrays.copyOfRange(buf, offset, offset+=4));
		System.out.println("Entry count = " + entryCount);

		for (int i = 0; i < entryCount.intValue(); i++) {
			System.out.println(HdlrAtomReader.handlerType);
			if (HdlrAtomReader.handlerType.equals("soun")) {
				
			} else if (HdlrAtomReader.handlerType.equals("vide")) {
				BigInteger size = new BigInteger(Arrays.copyOfRange(buf, offset, offset+=4));
				System.out.println("Size = " + size);
			} else if (HdlrAtomReader.handlerType.equals("hint")) {
				
			} else if (HdlrAtomReader.handlerType.equals("meta")) {
				
			}
		}
		
		return len;
/*		
		int rl = 0;

		System.out.println("Version = " + dis.readByte());
		rl += 1;t

		byte[] fbytes = new byte[3];
		dis.read(fbytes);
		rl += 3;
		System.out.println("Flags = " + fbytes[0] + fbytes[1] + fbytes[2]);
		
		int entries = dis.readInt();
		rl += 4;
		
		for (int i = 0; i < entries; i++) {
			int size = dis.readInt();
			System.out.println("Size = " + size);
			rl += 4;
			
			byte[] fourcc = new byte[4];
			dis.read(fourcc);
			System.out.println("Fourcc = " + new String(fourcc));
			rl += 4;
			
			for (int j = 0; j < (size-8); j++) {
				System.out.print(Integer.toHexString(dis.readByte()) + " ");
				rl++;
			}
		}
		
		System.out.println();
		return rl;
*/		
	}

}
