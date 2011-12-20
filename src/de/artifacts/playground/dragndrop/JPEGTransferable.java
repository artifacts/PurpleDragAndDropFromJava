package de.artifacts.playground.dragndrop;

import java.awt.datatransfer.*;
import java.awt.*;
import java.io.*;

/**
 * Allow a jpeg image to be copied/dragged from Java to native apps, in various forms (including
 * native PICT which is necessary on the Mac. This involves handcrafting a PICT header).
 * <p/>
 * Copyright (C) Square Box Systems Ltd. 2002-2004. All rights reserved.
 * Email: email@hidden
 */
public class JPEGTransferable implements Transferable {
    public static DataFlavor PICT_FLAVOR = new DataFlavor("image/x-pict", "Apple PICT Image");
    public static DataFlavor JPEG_FLAVOR = new DataFlavor("image/jpeg", "JPEG Image");
    public static DataFlavor IMAGE_FLAVOR = new DataFlavor("image/x-java-image; class=java.awt.Image", "Image");
    private static DataFlavor[] supportedFlavors = {PICT_FLAVOR, JPEG_FLAVOR, IMAGE_FLAVOR};

    private byte[] jpegData;
    private int width, height;

    private static final boolean DEBUG = false;
    private static final boolean DUMP_TO_FILE = false;

    public JPEGTransferable(byte[] jpegdata, int w, int h) {
        this.jpegData = jpegdata;
        this.width = w;
        this.height = h;
    }

    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
        if (DEBUG) System.out.println("JPEGTransferable.getData " + flavor);
        if (flavor.equals(PICT_FLAVOR))
            return new ByteArrayInputStream(jpegToPict(jpegData, width, height));
        else if (flavor.equals(JPEG_FLAVOR))
            return new ByteArrayInputStream(jpegData);
        else if (flavor.equals(IMAGE_FLAVOR))
            return Toolkit.getDefaultToolkit().createImage(jpegData);
        else {
            if (DEBUG) System.out.println("Unsupported");
            throw new UnsupportedFlavorException(flavor);
        }
    }

    public DataFlavor[] getTransferDataFlavors() {
        return supportedFlavors;
    }

    public boolean isDataFlavorSupported(DataFlavor flavor) {
        if (DEBUG) System.out.println("JPEGTransferable.isFlavorSupported " + flavor);
        for (int i = 0; i < supportedFlavors.length; ++i) {
            if (flavor.equals(supportedFlavors[i]))
                return true;
        }
        return false;
    }

    public static byte[] jpegToPict(byte[] jpegdata, int width, int height) {
        int dpi = 72;

        int paddingLen = 512; // only for PICT files
        int headerLen = 2 + 8 + 4;
        int headerOpLen = 2 + 24;
        int clipLen = 2 + 10;
        int qtHeaderLen = 2 + 4 + 2 + 48 + 2 + 16;
        int imDescLen = 4 + 4 + 8 + 8 * 4 + 2 + 32 + 2 + 2;
        int jpegLen = jpegdata.length;
        int alignment = jpegLen % 2;
        int tailLen = 0x210;
        int totalLen = headerLen + headerOpLen + clipLen + qtHeaderLen +
                imDescLen + jpegLen + alignment + tailLen;

        byte[] pict = new byte[totalLen];
        int pos = 0;

        if (DUMP_TO_FILE)
            pos += paddingLen;

        setInt2(pict, pos, totalLen - paddingLen);    // length
        setInt2(pict, pos + 6, height);
        setInt2(pict, pos + 8, width);
        setBytes(pict, pos + 10, new int[]{0, 0x11, 0x02, 0xFF});
        pos += headerLen;

        setBytes(pict, pos, new int[]{0x0C, 0x0, 0xFF, 0xFE});
        setInt2(pict, pos + 6, dpi);
        setInt2(pict, pos + 10, dpi);
        setInt2(pict, pos + 18, height);
        setInt2(pict, pos + 20, width);
        pos += headerOpLen;

        setBytes(pict, pos, new int[]{0x0, 0x01, 0x0, 0x0A});
        setInt2(pict, pos + 8, height);
        setInt2(pict, pos + 10, width);
        pos += clipLen;

        setInt2(pict, pos, 0x8200);
        setInt2(pict, pos + 4, totalLen - tailLen - (pos + 6));
        setInt2(pict, pos + 8, 0x0001);
        setInt2(pict, pos + 24, 0x0001);
        setInt2(pict, pos + 40, 0x4000);
        setInt2(pict, pos + 56, 0x0040);
        setInt2(pict, pos + 62, height);    // ??
        setInt2(pict, pos + 64, width);
        setInt2(pict, pos + 68, 0x3000);
        pos += qtHeaderLen;

        setInt2(pict, pos + 2, imDescLen);    // 0x56
        setBytes(pict, pos + 4, new int[]{'j', 'p', 'e', 'g'});
        setInt2(pict, pos + 16, 0x0001);
        setInt2(pict, pos + 18, 0x0001);
        setBytes(pict, pos + 20, new int[]{'a', 'p', 'p', 'l'});
        setInt2(pict, pos + 30, 512);    // spatial quality
        setInt2(pict, pos + 32, width);
        setInt2(pict, pos + 34, height);
        setInt2(pict, pos + 36, dpi);
        setInt2(pict, pos + 40, dpi);
        setInt2(pict, pos + 46, jpegLen);
        setInt2(pict, pos + 48, 0x0001);
        setPString(pict, pos + 50, "Photo - JPEG");
        setInt2(pict, pos + 82, 24);    // bit depth
        setInt2(pict, pos + 84, -1);    // color table
        pos += imDescLen;

        System.arraycopy(jpegdata, 0, pict, pos, jpegLen);
        pos += jpegLen + alignment;

//setInt2(pict, pos, 0x00FF);	// terminating early doesn't work with some apps, eg. Photoshop
        setBytes(pict, pos, trailer);    // instead, tack on a trailer we copied (not sure what it does...)

        if (DUMP_TO_FILE) {
// dump to PICT file (if we include 512 byte application area)
            try {
                ++seq;
                FileOutputStream os = new FileOutputStream("/tmp/clipboard" + seq + ".pict");
                os.write(pict);
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return pict;
    }

    private static int seq = 0;

    private static int[] trailer =
            {
                    0x0, 0x98, 0x0, 0xa, 0x0, 0x0, 0x0, 0x0, 0x0, 0x2c, 0x0, 0x45, 0x0, 0x0, 0x0, 0x0, 0x0, 0x2c, 0x0, 0x45, 0x0, 0x0, 0x0, 0x0, 0x0, 0xa, 0x0, 0xa, 0x0, 0x0, 0x8, 0xfd, 0xff, 0x2, 0x0, 0x0, 0xfe, 0xfe, 0x0, 0xb, 0x7, 0x80, 0x7f, 0xff, 0xff, 0x0, 0x7, 0xff, 0xc0, 0xff, 0x0, 0xb, 0x7, 0x80, 0x7f, 0xff, 0xff, 0x0,
                    0x1f, 0xff, 0xf0, 0xff, 0x0, 0xb, 0x7, 0x80, 0x7f, 0xff, 0xff, 0x0, 0x7f, 0xff, 0xfc, 0xff, 0x0, 0xb, 0x7, 0x80, 0x7f, 0xff, 0xff, 0x0, 0xff, 0xff, 0xfe, 0xff, 0x0, 0xa, 0x4, 0x80, 0x7f, 0xc0, 0xff, 0x1, 0xfe, 0xff, 0xff, 0x0, 0xb, 0x9, 0x88, 0x7f, 0x0, 0x3f, 0x3, 0xff, 0x1, 0xff, 0x80, 0x0, 0xb, 0x9, 0x88, 0x7e,
                    0x0, 0x1f, 0x3, 0xfc, 0x0, 0x7f, 0x80, 0x0, 0xb, 0x9, 0x88, 0x7c, 0x0, 0xf, 0x7, 0xf8, 0x0, 0x3f, 0xc0, 0x0, 0xb, 0x9, 0x80, 0x78, 0x1c, 0x7, 0x7, 0xf0, 0x0, 0x1f, 0xc0, 0x0, 0xb, 0x9, 0x80, 0x78, 0x1c, 0x7, 0xf, 0xe0, 0x0, 0xf, 0xe0, 0x0, 0xb, 0x9, 0x80, 0x70, 0x1c, 0x3, 0xf, 0xc0, 0x0, 0x7, 0xe0, 0x0, 0xb, 0x9,
                    0x80, 0x70, 0x1c, 0x3, 0x1f, 0xc0, 0x0, 0x7, 0xf0, 0x0, 0xb, 0x9, 0x80, 0x70, 0x1c, 0x3, 0x1f, 0x80, 0x0, 0x3, 0xf0, 0x0, 0xb, 0x9, 0x80, 0x70, 0x1c, 0x3, 0x1f, 0x80, 0x0, 0x3, 0xf0, 0x0, 0xb, 0x9, 0x80, 0x70, 0x1c, 0x3, 0x1f, 0x80, 0x3f, 0xfc, 0xf0, 0x0, 0xb, 0x9, 0x80, 0x70, 0x1c, 0x3, 0x1f, 0x80, 0x27, 0xfc,
                    0xf0, 0x0, 0xb, 0x9, 0x80, 0x70, 0x8, 0x3, 0x1f, 0x80, 0x3f, 0xfc, 0xf0, 0x0, 0xb, 0x9, 0x80, 0x70, 0x0, 0x3, 0x1f, 0x80, 0x0, 0x3, 0xf0, 0x0, 0xb, 0x9, 0x80, 0x70, 0x0, 0x3, 0x1f, 0x80, 0x0, 0x3, 0xf0, 0x0, 0xb, 0x9, 0x87, 0xf0, 0x1c, 0x3, 0x1f, 0xc0, 0x0, 0x7, 0xf0, 0x0, 0xb, 0x9, 0x81, 0xf0, 0x1c, 0x3, 0xf,
                    0xc0, 0x0, 0x7, 0xe0, 0x0, 0xb, 0x9, 0x81, 0xf0, 0x1c, 0x7, 0xf, 0xe0, 0x0, 0xf, 0xe0, 0x0, 0xb, 0x9, 0x81, 0xf0, 0x0, 0x7, 0x7, 0xf0, 0x0, 0x1f, 0xc0, 0x0, 0xb, 0x9, 0x81, 0xf0, 0x0, 0xf, 0x7, 0xf8, 0x0, 0x3f, 0xc0, 0x0, 0xb, 0x9, 0x81, 0xe0, 0x0, 0x1f, 0x3, 0xfc, 0x0, 0x7f, 0x80, 0x0, 0xb, 0x9, 0x8f, 0x80, 0x0,
                    0x7f, 0x3, 0xff, 0x1, 0xff, 0xc0, 0x0, 0xb, 0x0, 0x81, 0xfe, 0xff, 0x0, 0x1, 0xfe, 0xff, 0x1, 0xe0, 0x0, 0xb, 0x0, 0x81, 0xfe, 0xff, 0x0, 0x0, 0xfe, 0xff, 0x1, 0xf0, 0x0, 0xb, 0x0, 0x81, 0xfe, 0xff, 0x5, 0x0, 0x7f, 0xff, 0xff, 0xf0, 0x0, 0xb, 0x0, 0x81, 0xfe, 0xff, 0x5, 0x0, 0x1f, 0xff, 0xff, 0xf0, 0x0, 0x9,
                    0xfd, 0xff, 0x5, 0x0, 0x7, 0xff, 0xcf, 0xf0, 0x0, 0x8, 0xfc, 0x0, 0x4, 0x1, 0xff, 0x3, 0xf0, 0x0, 0x2, 0xf7, 0x0, 0xb, 0x9, 0x38, 0x0, 0xc, 0x7c, 0x0, 0x0, 0x3d, 0xcf, 0xf8, 0x0, 0xb, 0x9, 0x44, 0x2, 0x4, 0x54, 0x80, 0x0, 0x12, 0x91, 0xa8, 0x0, 0xb, 0x9, 0x82, 0x0, 0x4, 0x10, 0x0, 0x0, 0x12, 0xa0, 0x20, 0x0, 0xb,
                    0x9, 0x83, 0xb6, 0x75, 0x91, 0xb7, 0xd8, 0x1c, 0xa0, 0x20, 0x0, 0xb, 0x9, 0x82, 0x92, 0x95, 0x10, 0x9a, 0x64, 0x10, 0xa0, 0x20, 0x0, 0xb, 0x9, 0x82, 0x92, 0x87, 0x10, 0x92, 0x7c, 0x10, 0xa0, 0x20, 0x0, 0xb, 0x9, 0x44, 0x92, 0x85, 0x10, 0x92, 0x60, 0x10, 0x91, 0x20, 0x0, 0xb, 0x9, 0x38, 0xff, 0x7f, 0xb9, 0xfb, 0x7c, 0x39,
                    0xcf, 0x70, 0x0, 0x4, 0x0, 0x18, 0xf8, 0x0, 0x4, 0x0, 0xe, 0xf8, 0x0, 0x0, 0xff
            };

// Utility functions to manipulate byte arrays

    public static void setPString(byte[] data, int pos, String value) {
        data[pos] = (byte) value.length();
        for (int i = 0; i < value.length(); ++i)
            data[pos + 1 + i] = (byte) value.charAt(i);
    }

    public static void setInt2(byte[] data, int pos, int value) {
        byte l = (byte) value;
        byte h = (byte) (value / 256);
        data[pos] = h;
        data[pos + 1] = l;
    }

    public static void setBytes(byte[] data, int pos, int[] value) {
        for (int i = 0; i < value.length; ++i)
            data[pos + i] = (byte) value[i];
    }
}