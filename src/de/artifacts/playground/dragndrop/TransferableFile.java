package de.artifacts.playground.dragndrop;
import java.io.*;
import java.util.*;
import java.awt.datatransfer.*;

public class TransferableFile implements Transferable
{
   private List fileList ;

   public TransferableFile(List files)
   {
      fileList = files;
   }

   // Returns an object which represents the data to be transferred.
   public Object getTransferData(DataFlavor flavor)
      throws UnsupportedFlavorException
   {
      if( flavor.equals(DataFlavor.javaFileListFlavor) )
         return fileList ;

      throw new UnsupportedFlavorException(flavor);
   }

   // Returns an array of DataFlavor objects indicating the flavors
   // the data can be provided in.
   public DataFlavor[] getTransferDataFlavors()
   {
      return new DataFlavor[] {DataFlavor.javaFileListFlavor} ;
   }

   // Returns whether or not the specified data flavor is supported for this object.
   public boolean isDataFlavorSupported(DataFlavor flavor)
   {
      return flavor.equals(DataFlavor.javaFileListFlavor) ;
   }
}
