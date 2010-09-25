package siahu.iso14496.type;

import java.io.DataInputStream;
import java.io.IOException;

public interface AtomReader {

    int read(DataInputStream dis, int len) throws IOException;

}
