package com.example.linson.notepad;

import android.util.Xml;

import com.example.linson.notepad.domain.ContentBean;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by linson on 2017/3/8.
 */

public class XmlUtils {
    public static void saveToXml(List<ContentBean> list, OutputStream os) {
        XmlSerializer serializer = Xml.newSerializer();
        try {
            serializer.setOutput(os, "utf-8");
            serializer.startDocument("utf-8", true);
            serializer.startTag(null, "contents");
            for (int i = 0; i < list.size(); i++) {
                ContentBean contentBean = list.get(i);
                int id = contentBean.getId();
                String update = contentBean.getUpdate();
                String title = contentBean.getTitle();
                title = EncryptUtils.encryptAESString(title);
                String content = contentBean.getContent();
                content = EncryptUtils.encryptAESString(content);
                serializer.startTag(null, "item");

                serializer.startTag(null, "id");
                serializer.text(String.valueOf(id));
                serializer.endTag(null, "id");

                serializer.startTag(null, "update");
//                String strDate = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(update);
                serializer.text(update);
                serializer.endTag(null, "update");

                serializer.startTag(null, "title");
                serializer.text(title);
                serializer.endTag(null, "title");

                serializer.startTag(null, "content");
                serializer.text(content);
                serializer.endTag(null, "content");

                serializer.endTag(null, "item");
            }
            serializer.endTag(null, "contents");
            serializer.endDocument();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<ContentBean> readXmlFile(File file) {
        XmlPullParser parser = Xml.newPullParser();
        List<ContentBean> list = null;
        ContentBean bean = null;
        try {
            parser.setInput(new FileInputStream(file), "utf-8");
            int event = parser.next();
            while (event != XmlPullParser.END_DOCUMENT) {
                String tagName = parser.getName();
                if (event == XmlPullParser.START_TAG) {
                    if (tagName.equals("contents")) {
                        list = new ArrayList<>();
                    } else if (tagName.equals("item")) {
                        bean = new ContentBean();
                    } else if (tagName.equals("id")) {
                        bean.setId(Integer.parseInt(parser.nextText()));
                    } else if (tagName.equals("update")) {
                        bean.setUpdate(parser.nextText());
                    } else if (tagName.equals("title")) {
                        String title = parser.nextText();
                        title = EncryptUtils.decryptAESString(title);
                        bean.setTitle(title);
                    } else if (tagName.equals("content")) {
                        String content = parser.nextText();
                        content = EncryptUtils.decryptAESString(content);
                        bean.setContent(content);
                    }
                } else if (event == XmlPullParser.END_TAG) {
                    if (tagName.equals("item")) {
                        list.add(bean);
                    }
                }
                event = parser.next();
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
