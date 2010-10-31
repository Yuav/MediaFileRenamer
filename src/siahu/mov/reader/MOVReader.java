package siahu.mov.reader;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import siahu.iso14496.type.AtomReader;
import siahu.iso14496.type.AtomType;
import siahu.iso14496.type.BaseAtomReader;
import siahu.iso14496.type.Box;
import siahu.iso14496.type.ContainerBox;
import siahu.iso14496.type.FtypAtomReader;
import siahu.iso14496.type.HdlrAtomReader;
import siahu.iso14496.type.MdhdAtomReader;
import siahu.iso14496.type.MvhdAtomReader;
import siahu.iso14496.type.StcoAtomReader;
import siahu.iso14496.type.StscAtomReader;
import siahu.iso14496.type.StsdAtomReader;
import siahu.iso14496.type.StszAtomReader;
import siahu.iso14496.type.SttsAtomReader;
import siahu.iso14496.type.TkhdAtomReader;

public class MOVReader {

    private long streampos = 0;
    private HashMap<String, AtomType> atommap;
    private Logger logger;

    class PathEntry {
        String type;
        long pos;
    }

    private Stack<PathEntry> path;
    private Stack<ContainerBox> boxes;

    public MOVReader() {
        path = new Stack<PathEntry>();
        boxes = new Stack<ContainerBox>();
        boxes.push(new ContainerBox());
        atommap = new HashMap<String, AtomType>();
        atommap.put("AMBA", new AtomType("AMBA", AtomType.ATOM_LEAF,
                new BaseAtomReader()));
        atommap.put("clef", new AtomType("clef", AtomType.ATOM_LEAF,
                new BaseAtomReader()));
        atommap.put("ctts", new AtomType("ctts", AtomType.ATOM_LEAF,
                new BaseAtomReader()));
        atommap.put("dinf", new AtomType("dinf", AtomType.ATOM_CONTAINER, null));
        atommap.put("dref", new AtomType("dref", AtomType.ATOM_LEAF,
                new BaseAtomReader()));
        atommap.put("enof", new AtomType("enof", AtomType.ATOM_LEAF,
                new BaseAtomReader()));
        atommap.put("free", new AtomType("free", AtomType.ATOM_LEAF,
                new BaseAtomReader()));
        atommap.put("ftyp", new AtomType("ftyp", AtomType.ATOM_LEAF,
                new FtypAtomReader(), FtypAtomReader.class));
        atommap.put("hdlr", new AtomType("hdlr", AtomType.ATOM_LEAF,
                new HdlrAtomReader()));
        atommap.put("mdat", new AtomType("mdat", AtomType.ATOM_LEAF, null));
        atommap.put("mdhd", new AtomType("mdhd", AtomType.ATOM_LEAF,
                new MdhdAtomReader()));
        atommap.put("mdia", new AtomType("mdia", AtomType.ATOM_CONTAINER, null));
        atommap.put("minf", new AtomType("minf", AtomType.ATOM_CONTAINER, null));
        atommap.put("moov", new AtomType("moov", AtomType.ATOM_CONTAINER, null));
        atommap.put("mvhd", new AtomType("mvhd", AtomType.ATOM_LEAF,
                new MvhdAtomReader()));
        atommap.put("prof", new AtomType("prof", AtomType.ATOM_LEAF,
                new BaseAtomReader()));
        atommap.put("sdtp", new AtomType("sdtp", AtomType.ATOM_LEAF,
                new BaseAtomReader()));
        atommap.put("smhd", new AtomType("smhd", AtomType.ATOM_LEAF,
                new BaseAtomReader()));
        atommap.put("stbl", new AtomType("stbl", AtomType.ATOM_CONTAINER, null));
        atommap.put("stco", new AtomType("stco", AtomType.ATOM_LEAF,
                new StcoAtomReader()));
        atommap.put("stsc", new AtomType("stsc", AtomType.ATOM_LEAF,
                new StscAtomReader()));
        atommap.put("stsd", new AtomType("stsd", AtomType.ATOM_LEAF,
                new StsdAtomReader()));
        atommap.put("stss", new AtomType("stss", AtomType.ATOM_LEAF,
                new BaseAtomReader()));
        atommap.put("stsz", new AtomType("stsz", AtomType.ATOM_LEAF,
                new StszAtomReader()));
        atommap.put("stts", new AtomType("stts", AtomType.ATOM_LEAF,
                new SttsAtomReader()));
        atommap.put("tapt", new AtomType("tapt", AtomType.ATOM_CONTAINER, null));
        atommap.put("tkhd", new AtomType("tkhd", AtomType.ATOM_LEAF,
                new TkhdAtomReader()));
        atommap.put("trak", new AtomType("trak", AtomType.ATOM_CONTAINER, null));
        atommap.put("udta", new AtomType("udta", AtomType.ATOM_CONTAINER, null));
        atommap.put("vmhd", new AtomType("vmhd", AtomType.ATOM_LEAF,
                new BaseAtomReader()));
        logger = Logger.getLogger(this.getClass().getName());
    }

    public void read(File movFile) {
        if (logger.isLoggable(Level.FINEST)) {
            logger.finest(movFile.toString());
        }
        if (movFile.isFile() == false)
            return;
        DataInputStream dis = null;
        try {
            FileInputStream fis = new FileInputStream(movFile);
            dis = new DataInputStream(fis);
            while (true) {
                readAtom(dis);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (EOFException e) {
            if (logger.isLoggable(Level.FINEST)) {
                logger.finest("EOF");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (dis != null) {
                try {
                    dis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void readAtom(DataInputStream dis) throws IOException {
        int len = dis.readInt();
        streampos += 4;
        byte[] blen = new byte[4];
        dis.readFully(blen);
        streampos += 4;
        String type = new String(blen);
        String indent = "";
        int depth = path.size();
        for (int i = 0; i < depth; i++) {
            indent += "\t";
        }
        if (logger.isLoggable(Level.FINEST)) {
            logger.finest(indent + type + ", " + len);
        }
        AtomType atomType = atommap.get(type);
        if (atomType == null) {
            dis.skip(len - 8);
            streampos += (len - 8);
        } else if (atomType.getType() == AtomType.ATOM_CONTAINER) {
            PathEntry pe = new PathEntry();
            pe.type = type;
            pe.pos = streampos + (len - 8);
            path.push(pe);

            ContainerBox parent = boxes.peek();
            ContainerBox box = new ContainerBox();
            parent.addBox(box);
            boxes.push(box);
        } else if (atomType.getType() == AtomType.ATOM_LEAF) {
            int read = 0;
            Class<? extends Box> boxclass = atomType.getBox();
            if ((boxclass != null) && (boxes.isEmpty() == false)) {
                try {
                    Constructor<? extends Box> c = boxclass.getConstructor();
                    Box box = c.newInstance();
                    boxes.peek().addBox(box);
                } catch (SecurityException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
            AtomReader reader = atomType.getReader();
            if (reader != null) {
                read = reader.read(dis, len - 8);
            }
            dis.skip(len - 8 - read);
            streampos += (len - 8);
        }
        while (path.isEmpty() == false) {
            PathEntry pe = path.peek();
            if (streampos >= pe.pos) {
                path.pop();
            } else {
                break;
            }
        }
        readAtom(dis);
    }

    public static Calendar getCalendar(long seconds) {
        long useconds = (long) seconds & 0xFFFFFFFFL;
        int half = (int) (useconds >> 1); // work around signed integer issue
        Calendar cal = Calendar.getInstance();
        cal.set(1904, Calendar.JANUARY, 1, 0, 0, 0);
        cal.add(Calendar.SECOND, half);
        cal.add(Calendar.SECOND, half);
        return cal;
    }

    public static String formatDate(long seconds) {
        Calendar cal = getCalendar(seconds);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.ssss");
        return sdf.format(cal.getTime());
    }

    public static String bytes2hex(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() == 1) {
                hex = "0" + hex;
            }
            sb.append(hex.toUpperCase() + " ");
        }
        return sb.toString();
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        MOVReader reader = new MOVReader();
        reader.read(new File("/home/psiahu/Desktop/2009-10/20091001-190526.MOV"));
    }

}
