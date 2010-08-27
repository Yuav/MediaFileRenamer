package siahu.mediafile.renamer;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class JPGRenamer implements IMediaFileRenamer {

    private SimpleDateFormat fromSdf;
    private SimpleDateFormat toSdf;

    public JPGRenamer() {
        fromSdf = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
        toSdf = new SimpleDateFormat("yyyyMMdd-HHmmss");
    }

    @Override
    public boolean canHandle(RandomAccessFile file) throws IOException {
        file.seek(0);
        int b1 = file.readUnsignedShort();
        if (b1 == 0xFFD8) {
            return true;
        }
        return false;
    }

    @Override
    public String rename(RandomAccessFile file) throws IOException {
        String name = null;
        Map<String, Object> map = new HashMap<String, Object>();
        boolean lendian = false;
        file.seek(2);
        long exifOffset = file.getFilePointer();
//        System.out.println("Exif offset " + exifOffset);
        byte[] marker = new byte[2];
        file.readFully(marker);
        if ((marker[0] & 0xFF) == 0xFF) {
            int marker1 = (marker[1] & 0xFF);
            if ((marker1 == 0xE1) || (marker1 == 0xE0)) { // E1 = EXIF E0 = JFIF
                file.seek(12);
                lendian = ((file.readUnsignedShort() & 0xFFFF) == 0x4949);
                file.seek(20);
                readIDF(file, lendian, map);
            }
        }
//        System.out.println(map);
        name = (String) map.get("Date9003");
        if (name == null) {
            name = (String) map.get("Date132");
        }
        return name;
    }

    private void readIDF(RandomAccessFile file, boolean lendian, Map<String, Object> map) throws IOException {
        long exifSubIFD = 0;
        short entries = (short) endian2(file.readShort(), lendian);
        
        for (int i = 0; i < entries; i++) {
            int tag = endian2(file.readUnsignedShort(), lendian);
            int type = endian2(file.readUnsignedShort(), lendian);
            long count = endian4(file.readInt(), lendian);
            long offset = endian4(file.readInt(), lendian);
            if (1 == 2) {
                System.out.println("entry " + i + ", tag="
                        + Integer.toHexString(tag) + ", type="
                        + Integer.toHexString(type) + ", count="
                        + Integer.toHexString((int) count)
                        + ", offset="
                        + Integer.toHexString((int) offset));
            }
            if ((tag == 0x0132) || (tag == 0x9003))
            {
                long curoff = file.getFilePointer();
                file.seek(offset + 0x0C);
                byte[] date = new byte[(int) count];
                file.read(date);
                try {
                    String name = toSdf
                            .format(fromSdf.parse(new String(date)));
                    name = name + ".JPG";
                    map.put("Date" + Integer.toHexString(tag), name);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                file.seek(curoff);
                // return null;
            } else if (tag == 0x8769) {
                exifSubIFD = offset;
            }
        }

        long nextIFDOffset = endian4(file.readInt(), lendian);

        if (exifSubIFD > 0) {
//            System.out.println("ExifSubIFD offset = " + exifSubIFD);
            file.seek(0x0C + exifSubIFD);
            readIDF(file, lendian, map);
        }

        if (nextIFDOffset > 0) {
//            System.out.println("Next IFD " + nextIFDOffset);
            file.seek(0x0C + nextIFDOffset);
            readIDF(file, lendian, map);
        }
        /*
        if (exifIFD > 0) {
            file.seek(exifIFD + 0x0C);
            short entries2 = file.readShort();
            for (int i = 0; i < entries2; i++) {
                int tag = file.readUnsignedShort();
                // System.out.println("entry " + i + ", tag " +
                // Integer.toHexString(tag));
                if (tag == 0x9003) {
                    file.readShort();
                    int comp = file.readInt();
                    int offset = file.readInt();
                    file.seek(offset + 0x0C);
                    byte[] date = new byte[comp];
                    file.read(date);
                    try {
                        name = toSdf.format(fromSdf.parse(new String(
                                date)));
                        name = name + ".JPG";
                        map.put("Date9003", name);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
                file.skipBytes(10);
            }
        }
        */
    }
    
    private long endian4(int x, boolean lendian) {
        if (lendian) {
            int b1 = ((x & 0xFF000000) >> 24) & 0x000000FF;
            int b2 = ((x & 0x00FF0000) >> 8) & 0x0000FF00;
            int b3 = ((x & 0x0000FF00) << 8) & 0x00FF0000;
            int b4 = ((x & 0x000000FF) << 24) & 0xFF0000;
            int y = b1 | b2 | b3 | b4;
            return y & 0xFFFFFFFF;
        } else {
            return x;
        }
    }

    private int endian2(int x, boolean lendian) {
        if (lendian) {
            short y = (short) (((x & 0xFF00) >> 8) | ((x & 0x00FF) << 8));
            return y & 0xFFFF;
        } else {
            return x;
        }
    }
    
}
