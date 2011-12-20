package de.artifacts.playground.dragndrop;

import javax.swing.*;
import java.awt.datatransfer.*;
import java.awt.event.InputEvent;
import java.io.*;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: mic
 * Date: 20.12.11
 * Time: 12:12
 * To change this template use File | Settings | File Templates.
 */
public class PurpleTransferHandler extends TransferHandler {
    @Override
    protected Transferable createTransferable(JComponent jComponent) {
        File file = new File("/Users/mic/Desktop/clownfish.jpg");
        Transferable transferable = null;
        try {
            transferable = new JPEGTransferable(getBytesFromFile(file), 71, 80);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
/*
                Transferable() {
            public DataFlavor[] getTransferDataFlavors() {
                ArrayList<DataFlavor> flavors = new ArrayList<DataFlavor>();
                DataFlavor flavor = null; //new DataFlavor(Object.class, "X-test/test; class=<java.lang.Object>; foo=bar");
                //try {
                    flavor = new DataFlavor("application/STEAssetDragType", "de.artifacts.playground.dragndrop.PurpleImageDataProvider");//  ); //STECMSContentDragType;class=java.lang.String");
                    flavors.add(flavor);
                    //flavors.add(new DataFlavor("text/plain;charset=UTF-8;eoln=\"\\n\";terminators=0"));
                //} catch (ClassNotFoundException e) {
                //    e.printStackTrace();
                //}
                return flavors.toArray(new DataFlavor[]{});
            }

            public boolean isDataFlavorSupported(DataFlavor dataFlavor) {
                 return dataFlavor.equals("application/STEAssetDragType");
            }

            public Object getTransferData(DataFlavor dataFlavor) throws UnsupportedFlavorException, IOException {
                Class clazz = dataFlavor.getDefaultRepresentationClass();
                /*String json = "{ foo: \"bar\" }";
                InputStream is = null;
                try {
                    is = new ByteArrayInputStream(json.getBytes("UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                return is;
                return null;
            }
        };
*/
        return transferable;
    }

    @Override
    public void exportAsDrag(JComponent jComponent, InputEvent inputEvent, int i) {
        super.exportAsDrag(jComponent, inputEvent, i);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void exportToClipboard(JComponent jComponent, Clipboard clipboard, int i) throws IllegalStateException {
        super.exportToClipboard(jComponent, clipboard, i);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public boolean importData(TransferSupport transferSupport) {
        return super.importData(transferSupport);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public boolean importData(JComponent jComponent, Transferable transferable) {
        return super.importData(jComponent, transferable);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public boolean canImport(TransferSupport transferSupport) {
        return super.canImport(transferSupport);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public boolean canImport(JComponent jComponent, DataFlavor[] dataFlavors) {
        return super.canImport(jComponent, dataFlavors);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public int getSourceActions(JComponent jComponent) {
        return TransferHandler.COPY;
    }

    @Override
    public Icon getVisualRepresentation(Transferable transferable) {
        return super.getVisualRepresentation(transferable);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    protected void exportDone(JComponent jComponent, Transferable transferable, int i) {
        super.exportDone(jComponent, transferable, i);    //To change body of overridden methods use File | Settings | File Templates.
    }




    // Returns the contents of the file in a byte array.
public static byte[] getBytesFromFile(File file) throws IOException {
    InputStream is = new FileInputStream(file);

    // Get the size of the file
    long length = file.length();

    // You cannot create an array using a long type.
    // It needs to be an int type.
    // Before converting to an int type, check
    // to ensure that file is not larger than Integer.MAX_VALUE.
    if (length > Integer.MAX_VALUE) {
        // File is too large
    }

    // Create the byte array to hold the data
    byte[] bytes = new byte[(int)length];

    // Read in the bytes
    int offset = 0;
    int numRead = 0;
    while (offset < bytes.length
           && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
        offset += numRead;
    }

    // Ensure all the bytes have been read in
    if (offset < bytes.length) {
        throw new IOException("Could not completely read file "+file.getName());
    }

    // Close the input stream and return bytes
    is.close();
    return bytes;
}
}
