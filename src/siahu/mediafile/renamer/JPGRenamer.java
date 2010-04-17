package siahu.mediafile.renamer;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.ParseException;
import java.text.SimpleDateFormat;

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
		if (b1  == 0xFFD8) {
			return true;
		}
		return false;
	}

	@Override
	public String rename(RandomAccessFile file) throws IOException {
		boolean lendian = false;
		file.seek(2);
		byte[] marker = new byte[2];
		file.readFully(marker);
		if ((marker[0] & 0xFF) == 0xFF) {
			int marker1 = (marker[1] & 0xFF);
			if ((marker1 == 0xE1) || (marker1 == 0xE0)) { // E1 = EXIF   E0 = JFIF
				file.seek(12);
				lendian = ((file.readUnsignedShort() & 0xFFFF) == 0x4949);
				file.seek(20);
				int exifIFD = 0;
				short entries = (short) endian2(file.readShort(), lendian);
				for (int i = 0; i < entries; i++) {
					int tag = endian2(file.readUnsignedShort(), lendian);
					int type = endian2(file.readUnsignedShort(), lendian);
					long count = endian4(file.readInt(), lendian);
					long offset = endian4(file.readInt(), lendian);
					if (false) {
						System.out.println("entry " + i + ", tag=" + Integer.toHexString(tag) + ", type=" + Integer.toHexString(type)
								+ ", count=" + Integer.toHexString((int) count) + ", offset=" + Integer.toHexString((int) offset));
					}
					if (tag == 0x0132)
					{
						file.seek(offset + 0x0C);
						byte[] date = new byte[(int) count];
						file.read(date);
						String name = null;
						try {
							name = toSdf.format(fromSdf.parse(new String(date)));
							return name + ".JPG";
						} catch (ParseException e) {
							e.printStackTrace();
						}
						return null;
					} else if (tag == 0x8769) {
						exifIFD = (int) offset;
//						System.out.println("ExifIFD offset = " + exifIFD);
					}
				}
				if (exifIFD > 0) {
					file.seek(exifIFD + 0x0C);
					short entries2 = file.readShort();
					for (int i = 0; i < entries2; i++) {
						int tag = file.readUnsignedShort();
//						System.out.println("entry " + i + ", tag " + Integer.toHexString(tag));
						if (tag == 0x9003) {
							file.readShort();
							int comp = file.readInt();
							int offset = file.readInt();
							file.seek(offset + 0x0C);
							byte[] date = new byte[comp];
							file.read(date);
							String name = null;
							try {
								name = toSdf.format(fromSdf.parse(new String(date)));
								return name + ".JPG";
							} catch (ParseException e) {
								e.printStackTrace();
							}
							return null;
						}
						file.skipBytes(10);
					}
				}
			}
		}
		return null;
	}

	private long endian4(int x, boolean lendian) {
		if (lendian) {
			int b1 = ((x & 0xFF000000) >> 24) & 0x000000FF;
			int b2 = ((x & 0x00FF0000) >> 8) & 0x0000FF00;
			int b3 = ((x & 0x0000FF00) << 8) & 0x00FF0000;
			int b4 = ((x & 0x000000FF) << 24) & 0xFF0000;
			int y = b1 | b2 | b3 | b4;
			return y;
		} else {
			return x;
		}
	}

	private int endian2(int x, boolean lendian) {
		if (lendian) {
			short y = (short) (((x & 0xFF00) >> 8) | ((x & 0x00FF) << 8));
			return y;
		} else {
			return x;
		}
	}
}
