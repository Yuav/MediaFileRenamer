package siahu.mediafile.renamer;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MOVRenamer implements IMediaFileRenamer {

    static private int ATOM_LEAF = 1;
    static private int ATOM_CONTAINER = 2;
    static private int ATOM_UNKNOWN = 3;

    public MOVRenamer() {
    }

    @Override
    public boolean canHandle(RandomAccessFile file) throws IOException {
        file.seek(0);
        int b1 = file.readUnsignedShort();
        if (b1 == 0xFFD8) {
            return false;
        }
        return true;
    }

    @Override
    public String rename(RandomAccessFile file) throws IOException {
        file.seek(0);
        return readAtom(file) + ".MOV";
    }

    private String readAtom(RandomAccessFile dis) throws IOException {
        int len = dis.readInt();
        byte[] blen = new byte[4];
        dis.readFully(blen);
        String type = new String(blen);
        int atomType = getAtomType(type);
        if (atomType == ATOM_UNKNOWN) {
            dis.skipBytes(len - 8);
        } else if (atomType == ATOM_CONTAINER) {
        } else if (atomType == ATOM_LEAF) {
            if (type.equals("mvhd")) {
                dis.readByte(); // Version
                byte[] fbytes = new byte[3];
                dis.read(fbytes); // Flags
                long creationtime = (long) dis.readInt() & 0xFFFFFFFFL;
                int creationHalfTime = (int) (creationtime >> 1); // work around
                                                                  // signed
                                                                  // integer
                                                                  // issue
                Calendar cal = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HHmmss");
                cal.set(1904, Calendar.JANUARY, 1, 0, 0, 0);
                cal.add(Calendar.SECOND, creationHalfTime);
                cal.add(Calendar.SECOND, creationHalfTime);
                return sdf.format(cal.getTime());
            } else {
                dis.skipBytes(len - 8);
            }
        }
        return readAtom(dis);
    }

    private int getAtomType(String type) {
        if (type.equals("mvhd")) {
            return ATOM_LEAF;
        } else if (type.equals("moov")) {
            return ATOM_CONTAINER;
        } else {
            return ATOM_UNKNOWN;
        }
    }

}
