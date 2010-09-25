package siahu.mediafile.renamer;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Tester {

    /**
     * @param args
     */
    public static void main(String[] args) {
        File file = new File("/media/Seagate/RENAME");
        // File file = new
        // File("/media/LaCie/OnlineStorageSolution_backup2/Memories/2009");
        Lister lister = new Lister(new File[] { file });
        lister.list();
        // boolean commit = false;
        boolean commit = true;
        if (commit) {
            Renamer renamer = new Renamer();
            renamer.rename(lister.getRenameList());
        }
    }

    public static void main2(String[] args) {
        File dir = new File("/media/LaCie/Memories/2010/RENAME");
        File[] files = dir.listFiles();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HHmmss");
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            String name = file.getName();
            String ext = name.substring(name.lastIndexOf('.'));
            name = name.substring(0, name.length() - ext.length());
            try {
                Calendar cal = Calendar.getInstance();
                cal.setTime(sdf.parse(name));
                cal.add(Calendar.YEAR, 2);
                cal.add(Calendar.DAY_OF_YEAR, 13);
                cal.add(Calendar.HOUR_OF_DAY, 8);
                cal.add(Calendar.MINUTE, 40);
                name = sdf.format(cal.getTime());
                file.renameTo(new File(file.getParentFile(), name + ext));
            } catch (ParseException e) {
                System.err.println(e.getMessage());
            }
        }
    }
}
