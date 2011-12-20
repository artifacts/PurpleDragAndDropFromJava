package de.artifacts.playground.dragndrop;

import xmlwise.Plist;
import xmlwise.XmlParseException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

/**
 * Created by IntelliJ IDEA.
 * User: mic
 * Date: 20.12.11
 * Time: 14:05
 * To change this template use File | Settings | File Templates.
 */
public class PurpleImageDataProvider extends InputStream {

    private InputStream dummyData = null;

    public PurpleImageDataProvider() {
        System.out.print("init");
    }

    public PurpleImageDataProvider(Object value) throws XmlParseException {
        //String json = "{ foo: \"bar\" }";
        String plist = Plist.fromXml("<dict><key>name</key><string>foo</string></dict>").toString();
        try {
            dummyData = new ByteArrayInputStream(plist.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int read(byte[] bytes, int i, int i1) throws IOException {
        return dummyData.read(bytes, i, i1);
    }

    @Override
    public int read() throws IOException {
        return dummyData.read();
    }
}
