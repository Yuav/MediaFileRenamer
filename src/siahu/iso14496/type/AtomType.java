package siahu.iso14496.type;

public class AtomType {

    static public int ATOM_LEAF = 1;
    static public int ATOM_CONTAINER = 2;

    private String name;
    private int type;
    private AtomReader reader;
    private Class<? extends Box> box;

    public AtomType(String name, int type, AtomReader reader) {
        this.name = name;
        this.type = type;
        this.reader = reader;
        this.box = null;
    }

    public AtomType(String name, int type, AtomReader reader,
            Class<? extends Box> box) {
        this.name = name;
        this.type = type;
        this.reader = reader;
        this.box = box;
    }

    public String getName() {
        return name;
    }

    public int getType() {
        return type;
    }

    public AtomReader getReader() {
        return reader;
    }

    public Class<? extends Box> getBox() {
        return box;
    }
}
