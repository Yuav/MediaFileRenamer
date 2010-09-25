package siahu.iso14496.type;

import java.io.DataInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;

import siahu.mov.reader.MOVReader;

/**
 * <pre>
 * Box Type  : ‘tkhd’
 * Container : Track Box (‘trak’)
 * Mandatory : Yes
 * Quantity  : Exactly one
 * </pre>
 * <p>
 * This box specifies the characteristics of a single track. Exactly one Track
 * Header Box is contained in a track.
 * <p>
 * In the absence of an edit list, the presentation of a track starts at the
 * beginning of the overall presentation. An empty edit is used to offset the
 * start time of a track.
 * <p>
 * The default value of the track header flags for media tracks is 7
 * (track_enabled, track_in_movie, track_in_preview). If in a presentation all
 * tracks have neither track_in_movie nor track_in_preview set, then all tracks
 * shall be treated as if both flags were set on all tracks. Hint tracks should
 * have the track header flags set to 0, so that they are ignored for local
 * playback and preview.
 * <p>
 * The width and height in the track header are measured on a notional
 * ‘square’ (uniform) grid. Track video data is normalized to these
 * dimensions (logically) before any transformation or placement caused by a
 * layup or composition system. Track (and movie) matrices, if used, also
 * operate in this uniformly-scaled space.
 * 
 * @author psiahu
 * 
 */
public class TkhdAtomReader implements AtomReader {

    @Override
    public int read(DataInputStream dis, final int len) throws IOException {

        byte[] buf = new byte[len];
        dis.readFully(buf);
        System.out.println(MOVReader.bytes2hex(buf));

        // version is an integer that specifies the version of this box (0 or 1
        // in this specification)
        byte version = buf[0];
        System.out.println("Version = " + version);

        // flags is a 24-bit integer with flags; the following values are
        // defined:
        // Track_enabled: Indicates that the track is enabled. Flag value is
        // 0x000001. A disabled track (the low
        // bit is zero) is treated as if it were not present.
        // Track_in_movie: Indicates that the track is used in the presentation.
        // Flag value is 0x000002.
        // Track_in_preview: Indicates that the track is used when previewing
        // the presentation. Flag value is
        // 0x000004.

        System.out.println("Flags = " + buf[1] + buf[2] + buf[3]);

        int offset = 4;

        // creation_time is an integer that declares the creation time of this
        // track (in seconds since midnight,
        // Jan. 1, 1904, in UTC time)
        BigInteger creationTime = null;
        if (version == 0x00) {
            creationTime = new BigInteger(Arrays.copyOfRange(buf, offset,
                    offset += 4));
        } else if (version == 0x01) {
            creationTime = new BigInteger(Arrays.copyOfRange(buf, offset,
                    offset += 8));
        }
        System.out.println("Creation time = "
                + MOVReader.formatDate(creationTime.longValue()));

        // modification_time is an integer that declares the most recent time
        // the track was modified (in
        // seconds since midnight, Jan. 1, 1904, in UTC time)
        BigInteger modificationTime = null;
        if (version == 0x00) {
            modificationTime = new BigInteger(Arrays.copyOfRange(buf, offset,
                    offset += 4));
        } else if (version == 0x01) {
            modificationTime = new BigInteger(Arrays.copyOfRange(buf, offset,
                    offset += 8));
        }
        System.out.println("Modification time = "
                + MOVReader.formatDate(modificationTime.longValue()));

        // track_ID is an integer that uniquely identifies this track over the
        // entire life-time of this presentation.
        // Track IDs are never re-used and cannot be zero.
        BigInteger trackID = new BigInteger(Arrays.copyOfRange(buf, offset,
                offset += 4));
        System.out.println("Track ID = " + trackID);

        offset += 4; // reserved

        // duration is an integer that indicates the duration of this track (in
        // the timescale indicated in the Movie
        // Header Box). The value of this field is equal to the sum of the
        // durations of all of the track’s edits. If
        // there is no edit list, then the duration is the sum of the sample
        // durations, converted into the timescale
        // in the Movie Header Box. If the duration of this track cannot be
        // determined then duration is set to all
        // 1s (32-bit maxint).
        BigInteger duration = null;
        if (version == 0x00) {
            duration = new BigInteger(Arrays.copyOfRange(buf, offset,
                    offset += 4));
        } else if (version == 0x01) {
            duration = new BigInteger(Arrays.copyOfRange(buf, offset,
                    offset += 8));
        }
        System.out.println("Duration = " + duration);

        offset += 8; // reserved

        // layer specifies the front-to-back ordering of video tracks; tracks
        // with lower numbers are closer to the
        // viewer. 0 is the normal value, and -1 would be in front of track 0,
        // and so on.
        BigInteger layer = new BigInteger(Arrays.copyOfRange(buf, offset,
                offset += 2));
        System.out.println("Layer = " + layer);

        // alternate_group is an integer that specifies a group or collection of
        // tracks. If this field is 0 there is no
        // information on possible relations to other tracks. If this field is
        // not 0, it should be the same for tracks
        // that contain alternate data for one another and different for tracks
        // belonging to different such groups.
        // Only one track within an alternate group should be played or streamed
        // at any one time, and must be
        // distinguishable from other tracks in the group via attributes such as
        // bitrate, codec, language, packet
        // size etc. A group may have only one member.
        BigInteger alternateGroup = new BigInteger(Arrays.copyOfRange(buf,
                offset, offset += 2));
        System.out.println("Alternate group = " + alternateGroup);

        // volume is a fixed 8.8 value specifying the track's relative audio
        // volume. Full volume is 1.0 (0x0100) and
        // is the normal value. Its value is irrelevant for a purely visual
        // track. Tracks may be composed by
        // combining them according to their volume, and then using the overall
        // Movie Header Box volume
        // setting; or more complex audio composition (e.g. MPEG-4 BIFS) may be
        // used.
        BigInteger volumex = new BigInteger(Arrays.copyOfRange(buf, offset,
                offset += 1));
        BigInteger volumey = new BigInteger(Arrays.copyOfRange(buf, offset,
                offset += 1));
        System.out.println("Volume = " + volumex + "." + volumey);

        offset += 2; // reserved

        // matrix provides a transformation matrix for the video; (u,v,w) are
        // restricted here to (0,0,1), hex
        // (0,0,0x40000000).
        offset += 36;

        // width and height specify the track's visual presentation size as
        // fixed-point 16.16 values. These need
        // not be the same as the pixel dimensions of the images, which is
        // documented in the sample
        // description(s); all images in the sequence are scaled to this size,
        // before any overall transformation of
        // the track represented by the matrix. The pixel dimensions of the
        // images are the default values.
        BigInteger widthx = new BigInteger(Arrays.copyOfRange(buf, offset,
                offset += 2));
        BigInteger widthy = new BigInteger(Arrays.copyOfRange(buf, offset,
                offset += 2));
        BigInteger heightx = new BigInteger(Arrays.copyOfRange(buf, offset,
                offset += 2));
        BigInteger heighty = new BigInteger(Arrays.copyOfRange(buf, offset,
                offset += 2));
        System.out.println("Width, Height = " + widthx + "." + widthy + ", "
                + heightx + "." + heighty);

        return len;
    }

}
