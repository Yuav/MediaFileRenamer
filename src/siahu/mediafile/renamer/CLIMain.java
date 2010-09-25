package siahu.mediafile.renamer;

import java.io.File;
import java.io.IOException;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CLIMain {

    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Please specify media directory");
            System.exit(1);
        }

        Logger logger = Logger.getLogger("");
        logger.setLevel(Level.INFO);
        for (Handler handler : logger.getHandlers()) {
            handler.setLevel(Level.INFO);
        }

        File file = new File(args[0]);
        Lister lister = new Lister(new File[] { file });
        lister.list();

        boolean commit = false;
        System.out.print("OK to proceed? (y/N) ");
        try {
            int reply = System.in.read();
            if (logger.isLoggable(Level.FINER)) {
                logger.finer(Integer.toString(reply));
            }
            if ((reply == 121) || (reply == 89)) {
                commit = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (commit) {
            Renamer renamer = new Renamer();
            renamer.rename(lister.getRenameList());
        }
    }

}
