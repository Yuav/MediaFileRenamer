package siahu.iso14496.type;

import java.io.DataInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import siahu.mov.reader.MOVReader;

/**
 * <h2>Definition</h2>
 * 
 * <pre>
 * Box Type  : ‘mvhd’
 * Container : Movie Box (‘moov’)
 * Mandatory : Yes
 * Quantity  : Exactly one
 * </pre>
 * 
 * <p>
 * This box defines overall information which is media-independent, and relevant
 * to the entire presentation considered as a whole.
 * 
 * <h2>Syntax</h2>
 * 
 * <pre>
 * aligned(8) class MovieHeaderBox extends FullBox(‘mvhd’, version, 0) {
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
 *    template int(32) rate = 0x00010000; // typically 1.0
 *    template int(16) volume = 0x0100;    // typically, full volume
 *    const bit(16) reserved = 0;
 *    const unsigned int(32)[2] reserved = 0;
 *    template int(32)[9] matrix =
 *       { 0x00010000,0,0,0,0x00010000,0,0,0,0x40000000 };
 *       // Unity matrix
 *    bit(32)[6] pre_defined = 0;
 *    unsigned int(32) next_track_ID;
 * }
 * </pre>
 * 
 * <h2>Semantics</h2>
 * <p>
 * <code>version</code> is an integer that specifies the version of this box (0
 * or 1 in this specification)
 * <p>
 * <code>creation_time</code> is an integer that declares the creation time of
 * the presentation (in seconds since midnight, Jan. 1, 1904, in UTC time)
 * <p>
 * <code>modification_time</code> is an integer that declares the most recent
 * time the presentation was modified (in seconds since midnight, Jan. 1, 1904,
 * in UTC time)
 * <p>
 * <code>timescale</code> is an integer that specifies the time-scale for the
 * entire presentation; this is the number of time units that pass in one
 * second. For example, a time coordinate system that measures time in sixtieths
 * of a second has a time scale of 60.
 * <p>
 * <code>duration</code> is an integer that declares length of the presentation
 * (in the indicated timescale). This property is derived from the
 * presentation’s tracks: the value of this field corresponds to the duration
 * of the longest track in the presentation.
 * <p>
 * <code>rate</code> is a fixed point 16.16 number that indicates the preferred
 * rate to play the presentation; 1.0 (0x00010000) is normal forward playback
 * <p>
 * <code>volume</code> is a fixed point 8.8 number that indicates the preferred
 * playback volume. 1.0 (0x0100) is full volume.
 * <p>
 * <code>matrix</code> provides a transformation matrix for the video; (u,v,w)
 * are restricted here to (0,0,1), hex values (0,0,0x40000000).
 * <p>
 * <code>next_track_ID</code> is a non-zero integer that indicates a value to
 * use for the track ID of the next track to be added to this presentation. Zero
 * is not a valid track ID value. The value of next_track_ID shall be larger
 * than the largest track-ID in use. If this value is equal to all 1s (32-bit
 * maxint), and a new media track is to be added, then a search must be made in
 * the file for an unused track identifier.
 * 
 * @author psiahu
 * 
 */
public class MvhdAtomReader implements AtomReader {
    
    private Logger logger;

    public MvhdAtomReader() {
        logger = Logger.getLogger(this.getClass().getName());
    }

    @Override
    public int read(DataInputStream dis, final int len) throws IOException {

        byte[] buf = new byte[len];
        dis.readFully(buf);
        if (logger.isLoggable(Level.FINEST)) {
            logger.finest(MOVReader.bytes2hex(buf));
        }

        byte version = buf[0];
        if (logger.isLoggable(Level.FINEST)) {
            logger.finest("Version = " + version);
            logger.finest("Flags = " + buf[1] + buf[2] + buf[3]);
        }

        int offset = 4;

        BigInteger creationTime = null;
        if (version == 0x00) {
            creationTime = new BigInteger(Arrays.copyOfRange(buf, offset,
                    offset += 4));
        } else if (version == 0x01) {
            creationTime = new BigInteger(Arrays.copyOfRange(buf, offset,
                    offset += 8));
        }
        if (logger.isLoggable(Level.FINEST)) {
            logger.finest("Creation time = "
                + MOVReader.formatDate(creationTime.longValue()));
        }

        BigInteger modificationTime = null;
        if (version == 0x00) {
            modificationTime = new BigInteger(Arrays.copyOfRange(buf, offset,
                    offset += 4));
        } else if (version == 0x01) {
            modificationTime = new BigInteger(Arrays.copyOfRange(buf, offset,
                    offset += 8));
        }
        if (logger.isLoggable(Level.FINEST)) {
            logger.finest("Modification time = "
                + MOVReader.formatDate(modificationTime.longValue()));
        }

        BigInteger timescale = new BigInteger(Arrays.copyOfRange(buf, offset,
                offset += 4));
        if (logger.isLoggable(Level.FINEST)) {
            logger.finest("Time scale = " + timescale);
        }

        BigInteger duration = null;
        if (version == 0x00) {
            duration = new BigInteger(Arrays.copyOfRange(buf, offset,
                    offset += 4));
        } else if (version == 0x01) {
            duration = new BigInteger(Arrays.copyOfRange(buf, offset,
                    offset += 8));
        }
        if (logger.isLoggable(Level.FINEST)) {
            logger.finest("Duration = " + duration);
        }

        BigInteger ratex = new BigInteger(Arrays.copyOfRange(buf, offset,
                offset += 2));
        BigInteger ratey = new BigInteger(Arrays.copyOfRange(buf, offset,
                offset += 2));
        if (logger.isLoggable(Level.FINEST)) {
            logger.finest("Rate = " + ratex + "." + ratey);
        }

        BigInteger volumex = new BigInteger(Arrays.copyOfRange(buf, offset,
                offset += 1));
        BigInteger volumey = new BigInteger(Arrays.copyOfRange(buf, offset,
                offset += 1));
        if (logger.isLoggable(Level.FINEST)) {
            logger.finest("Volume = " + volumex + "." + volumey);
        }

        offset += 2; // reserved
        offset += 8; // reserved

        offset += 36; // matrix

        offset += 24; // reserved

        BigInteger nextTrackId = new BigInteger(Arrays.copyOfRange(buf, offset,
                offset += 4));
        if (logger.isLoggable(Level.FINEST)) {
            logger.finest("Next track id = " + nextTrackId);
        }

        return len;
    }

}
