package siahu.media;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.URLTransfer;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import siahu.media.base.MediaIdentifier;
import siahu.media.base.MediaReader;
import siahu.media.builtin.MOVMediaIdentifier;
import siahu.media.builtin.RIFFMediaIdentifier;

public class MainApp {

    static private List<MediaIdentifier> mis;

    /**
     * @param args
     */
    public static void main(String[] args) {

        mis = new ArrayList<MediaIdentifier>();
        mis.add(new MOVMediaIdentifier());
        mis.add(new RIFFMediaIdentifier());

        Display display = new Display();
        Shell shell = new Shell(display);
        shell.setText("Hello world!");
        FillLayout fillLayout = new FillLayout(SWT.HORIZONTAL);
        shell.setLayout(fillLayout);

        Label dropLabel = new Label(shell, SWT.BORDER);
        dropLabel.setText("Hit me");
        DropTarget dropTarget = new DropTarget(dropLabel, DND.DROP_MOVE
                | DND.DROP_COPY | DND.DROP_LINK);
        dropTarget.setTransfer(new Transfer[] { URLTransfer.getInstance(),
                FileTransfer.getInstance() });
        dropTarget.addDropListener(new DropTargetAdapter() {
            @Override
            public void dragEnter(DropTargetEvent event) {
                System.out.println("dragEnter " + event.data);
            }

            @Override
            public void drop(DropTargetEvent event) {
                String[] fl = (String[]) event.data;
                MediaReader reader = null;
                Iterator<MediaIdentifier> it = mis.iterator();
                while (it.hasNext()) {
                    MediaIdentifier mi = it.next();
                    try {
                        reader = mi.getReader(new File(fl[0]));
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    if (reader != null) {
                        break;
                    }
                }
                if (reader == null) {
                    System.err.println("Media not identified");
                }
            }

            @Override
            public void dragOperationChanged(DropTargetEvent event) {
                System.out.println("dragOperationChanged");
            }
        });
        shell.pack();
        shell.open();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch())
                display.sleep();
        }
        display.dispose();
    }

}
