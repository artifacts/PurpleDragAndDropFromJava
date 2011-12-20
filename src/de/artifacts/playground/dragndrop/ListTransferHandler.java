package de.artifacts.playground.dragndrop;

import javax.swing.*;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.util.Vector;

public class ListTransferHandler extends TransferHandler
{
   private String dir;

   public ListTransferHandler()
   {
   }

   public ListTransferHandler(String dir)
   {
      if( dir.endsWith("/") )
         this.dir = dir;
      else
         this.dir = dir+"/";
   }

   public Transferable createTransferable(JComponent c)
   {
      JList list = (JList) c;  // we know it's a JList
      Object[] values = list.getSelectedValues();  // strings
      Vector files = new Vector();
      String listEntry ;
      for(int i=0; i<values.length; i++)
      {
         listEntry = (String)values[i];
         if( listEntry.startsWith("<dir>") )
            continue;

         listEntry = listEntry.substring(7);
         System.out.println(dir + listEntry);
         files.add( new File(dir + listEntry) );
      }
      TransferableFile  tf = new TransferableFile(files);
      return tf;
   }

   public int getSourceActions(JComponent c)
   {
      return COPY ;
   }

   /*
   public void exportToClipboard(JComponent comp, Clipboard clip, int action)
      throws IllegalStateException
   {
      System.out.println("exportToClipboard");
      super.exportToClipboard(comp,clip,action);
   }

   // Causes the Swing drag support to be initiated.
   public void exportAsDrag(JComponent comp, java.awt.event.InputEvent e, int action)
   {
      System.out.println("exportAsDrag");
      super.exportAsDrag(comp, e, action);
   }

   //Invoked after data has been exported.
   public void exportDone(JComponent source, Transferable data, int action)
   {
      System.out.println("exportDone");
      super.exportDone(source, data, action) ;
   }
   */

} // end class ListTransferHandler extends TransferHandler
