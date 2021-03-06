package siahu.iso14496.type;

import java.io.DataInputStream;
import java.io.IOException;

public class BaseAtomReader extends Box implements AtomReader {

    public BaseAtomReader() {
    }

    @Override
    public int read(DataInputStream dis, final int len) throws IOException {
        byte[] buf = new byte[len];
        dis.readFully(buf);

        return len;
    }

}
