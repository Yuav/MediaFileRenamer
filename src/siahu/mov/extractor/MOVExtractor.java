package siahu.mov.extractor;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.math.BigInteger;
import java.util.ArrayList;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class MOVExtractor {

    static public class Entry {
        long offset;
        long size;
    }

    private static Entry[] find(RandomAccessFile file, String boxtype,
            Entry boundary) throws IOException {
        if ((file == null) || (boxtype == null)) {
            return new Entry[0];
        }
        file.seek(boundary.offset);
        ArrayList<Entry> list = new ArrayList<Entry>();
        while (true) {
            long p = file.getFilePointer();
            if (p >= boundary.size) {
                break;
            }
            byte[] size = new byte[4];
            file.readFully(size);
            BigInteger biSize = new BigInteger(size);
            byte[] type = new byte[4];
            file.readFully(type);
            String sType = new String(type);
            if (boxtype.equals(sType)) {
                MOVExtractor.Entry e = new MOVExtractor.Entry();
                e.offset = p + 8;
                e.size = biSize.longValue() + e.offset - 8;
                list.add(e);
            }
            file.skipBytes(biSize.intValue() - 8);
        }
        return (Entry[]) list.toArray(new Entry[0]);
    }

    private static Entry findPath(RandomAccessFile file, String path,
            Entry boundary) throws IOException {
        String[] boxes = path.split("/");
        Entry b = boundary;
        for (int i = 0; i < boxes.length; i++) {
            System.out.println(boxes[i]);
            b = find(file, boxes[i], b)[0];
        }
        return b;
    }

    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {

        RandomAccessFile file = new RandomAccessFile(new File(
                "/home/psiahu/test2.mov"), "r");
        RandomAccessFile outfile = new RandomAccessFile(new File(
                "/home/psiahu/video.raw"), "rw");

        AudioFormat format = new AudioFormat(48000, 16, 2, true, true);
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
        SourceDataLine line = null;
        try {
            line = (SourceDataLine) AudioSystem.getLine(info);
            line.open(format);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
        line.start();

        Entry boundary = new Entry();
        boundary.offset = 0;
        boundary.size = file.length();
        Entry[] p = find(file, "moov", boundary);
        for (int i = 0; i < p.length; i++) {
            System.out.println(p[i].offset + ", " + p[i].size);
            Entry[] q = find(file, "trak", p[i]);

            // Extract video
            int j = 0;
            int spc = 1;
            int ss = 1382400;
            Entry b = findPath(file, "mdia/minf/stbl/stco", q[j]);
            file.seek(b.offset);
            file.skipBytes(4);
            int entryCount = file.readInt();
            for (int k = 0; k < entryCount; k++) {
                int coff = file.readInt();
                long pos = file.getFilePointer();
                file.seek(coff);
                byte[] buf = new byte[spc * ss];
                file.readFully(buf);
                // mplayer -demuxer rawvideo -rawvideo w=1280:h=720 video.raw
                outfile.write(buf, 0, buf.length);
                file.seek(pos);
            }

            // Play audio
            j = 1;
            spc = 1024;
            ss = 4;
            b = findPath(file, "mdia/minf/stbl/stco", q[j]);
            file.seek(b.offset);
            file.skipBytes(4);
            entryCount = file.readInt();
            for (int k = 0; k < entryCount; k++) {
                int coff = file.readInt();
                long pos = file.getFilePointer();
                file.seek(coff);
                byte[] buf = new byte[spc * ss];
                file.readFully(buf);
                line.write(buf, 0, buf.length);
                file.seek(pos);
            }
        }
        /*
         * try { while(true) { byte[] size = new byte[4]; file.readFully(size);
         * BigInteger biSize = new BigInteger(size); byte[] type = new byte[4];
         * file.readFully(type); String sType = new String(type); if
         * (sType.equals("mdat")) { for (int i = 0; i < co.length; i++) {
         * file.seek(co[i]); byte[] buf = new byte[spc * ss];
         * file.readFully(buf); // outfile.write(buf); int offset = 0; for (int
         * j = 0; j < spc; j++) { outfile.writeBytes(new
         * BigInteger(Arrays.copyOfRange(buf, offset, offset+=4)).toString() +
         * "\n"); } } break; } file.skipBytes(biSize.intValue() - 8); } }
         * catch(EOFException x) { x.printStackTrace(); } finally {
         * file.close(); outfile.close(); }
         */
    }

}
