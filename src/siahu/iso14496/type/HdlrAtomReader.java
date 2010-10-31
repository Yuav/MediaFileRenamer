package siahu.iso14496.type;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import siahu.mov.reader.MOVReader;

/**
 * <h2>Definition</h2>
 * 
 * <pre>
 * Box Type:      ‘hdlr’
 * Container:     Media Box (‘mdia’) or Meta Box (‘meta’)
 * Mandatory:     Yes
 * Quantity:      Exactly one
 * </pre>
 * 
 * <p>
 * This box within a Media Box declares the process by which the media-data in
 * the track is presented, and thus, the nature of the media in a track. For
 * example, a video track would be handled by a video handler.
 * 
 * <p>
 * This box when present within a Meta Box, declares the structure or format of
 * the 'meta' box contents.
 * 
 * <p>
 * There is a general handler for metadata streams of any type; the specific
 * format is identified by the sample entry, as for video or audio, for example.
 * If they are in text, then a MIME format is supplied to document their format;
 * if in XML, each sample is a complete XML document, and the namespace of the
 * XML is also supplied.
 * 
 * <p>
 * NOTE : MPEG-7 streams, which are a specific kind of metadata stream, have
 * their own handler declared, documented in the MP4 file format [ISO/IEC
 * 14496-14].
 * 
 * <p>
 * NOTE : metadata tracks are linked to the track they describe using a
 * track-reference of type ‘cdsc’. Metadata tracks use a null media header
 * (‘nmhd’), as defined in subclause 8.4.5.5.
 * 
 * @author psiahu
 * 
 */
public class HdlrAtomReader implements AtomReader {

    static public String handlerType;
    private Logger logger;

    public HdlrAtomReader() {
        logger = Logger.getLogger(this.getClass().getName());
    }
    
    @Override
    public int read(DataInputStream dis, final int len) throws IOException {

        byte[] buf = new byte[len];
        dis.readFully(buf);
        if (logger.isLoggable(Level.FINEST)) {
            logger.finest(MOVReader.bytes2hex(buf));
        }

        // version is an integer that specifies the version of this box
        byte version = buf[0];
        if (logger.isLoggable(Level.FINEST)) {
            logger.finest("Version = " + version);
            logger.finest("Flags = " + buf[1] + buf[2] + buf[3]);
        }

        int offset = 4;

        offset += 4; // reserved

        // handler_type when present in a media box, is an integer containing
        // one of the following values, or a
        // value from a derived specification:
        // ‘vide’ Video track
        // ‘soun’ Audio track
        // ‘hint’ Hint track
        // ‘meta’ Timed Metadata track
        // handler_type when present in a meta box, contains an appropriate
        // value to indicate the format of the
        // meta box contents. The value ‘null’ can be used in the primary
        // meta box to indicate that it is
        // merely being used to hold resources.
        String handlerType = new String(Arrays.copyOfRange(buf, offset,
                offset += 4));
        if (logger.isLoggable(Level.FINEST)) {
            logger.finest("Handler type = " + handlerType);
        }
        HdlrAtomReader.handlerType = handlerType;

        offset += 12; // reserved

        // name is a null-terminated string in UTF-8 characters which gives a
        // human-readable name for the track
        // type (for debugging and inspection purposes).
        byte namelength = buf[offset++];
        String name = new String(Arrays.copyOfRange(buf, offset,
                offset += namelength));
        if (logger.isLoggable(Level.FINEST)) {
            logger.finest("Name = " + name);
        }

        return len;
    }

}
