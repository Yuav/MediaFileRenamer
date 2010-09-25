package siahu.iso14496.type;

/**
 * <p>
 * Boxes start with a header which gives both size and type. The header permits
 * compact or extended size (32 or 64 bits) and compact or extended types (32
 * bits or full Universal Unique IDentifiers, i.e. UUIDs). The standard boxes
 * all use compact types (32-bit) and most boxes will use the compact (32-bit)
 * size. Typically only the Media Data Box(es) need the 64-bit size.
 * 
 * <p>
 * The size is the entire size of the box, including the size and type header,
 * fields, and all contained boxes. This facilitates general parsing of the
 * file.
 * 
 * <p>
 * The definitions of boxes are given in the syntax description language (SDL)
 * defined in MPEG-4 (see reference in clause 2). Comments in the code fragments
 * in this specification indicate informative material.
 * 
 * <p>
 * The fields in the objects are stored with the most significant byte first,
 * commonly known as network byte order or big-endian format. When fields
 * smaller than a byte are defined, or fields span a byte boundary, the bits are
 * assigned from the most significant bits in each byte to the least
 * significant. For example, a field of two bits followed by a field of six bits
 * has the two bits in the high order bits of the byte.
 * 
 * <h2>Syntax</h2>
 * 
 * <pre>
 * aligned(8) class Box (unsigned int(32) boxtype,
 *          optional unsigned int(8)[16] extended_type) {
 *    unsigned int(32) size;
 *    unsigned int(32) type = boxtype;
 *    if (size==1) {
 *       unsigned int(64) largesize;
 *    } else if (size==0) {
 *       // box extends to end of file
 *    }
 *    if (boxtype==‘uuid’) {
 *       unsigned int(8)[16] usertype = extended_type;
 *    }
 * }
 * 
 * <pre>
 * 
 * @author psiahu
 * 
 */
public abstract class Box {

    protected ContainerBox parent;

    public Box() {
    }

    protected void setParent(ContainerBox parent) {
        this.parent = parent;
    }

    public ContainerBox getParent() {
        return parent;
    }

}
