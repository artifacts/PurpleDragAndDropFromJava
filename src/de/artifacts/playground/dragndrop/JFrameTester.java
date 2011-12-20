package de.artifacts.playground.dragndrop;

import javax.swing.*;
import java.awt.*;

public class JFrameTester {

  public static void main(String[] args) {
    
    JFrame f = new JFrame();
    f.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    f.setLayout( new BorderLayout() );
    JTree tree = new JTree();
    tree.setDragEnabled( true );
    PurpleTransferHandler purpleTransferHandler = new PurpleTransferHandler();
    tree.setTransferHandler(purpleTransferHandler);
    f.add( tree, BorderLayout.PAGE_START );
    JTextArea textArea = new JTextArea();
    f.add( new JScrollPane(textArea) );
    f.setSize( 300, 300 );
    f.setVisible( true );

  }
  
}