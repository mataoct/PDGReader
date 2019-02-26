/*
 * 功能描述：解析图书元数据信息
 * Author：liujun
 */
package com.chaoxing.util;


import com.chaoxing.bean.Book;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;


public class MetaDataHandler extends DefaultHandler {
    private Book metaData = new Book();
    private StringBuffer buf = new StringBuffer();

    public Book getBookMetaData() {
        return metaData;
    }

    @Override
    public void startDocument() throws SAXException {

    }

    @Override
    public void endDocument() throws SAXException {

    }

    @Override
    public void startElement(String namespaceURI, String localName,
                             String qName, Attributes atts) throws SAXException {

    }

    @Override
    public void endElement(String namespaceURI, String localName,
                           String qName) throws SAXException {
        String value = buf.toString();
        value = value.replace("\n", "");
        if (localName.equals("ssid")) {
            metaData.setSsid(value);
        } else if (localName.equals("title")) {
            metaData.setTitle(value);
        } else if (localName.equals("creator")) {
            metaData.setAuthor(value);
        } else if (localName.equals("publisher")) {
            metaData.setPublisher(value);
        } else if (localName.equals("date")) {
            metaData.setPublishdate(value);
        } else if (localName.equals("pages")) {
            //String value = buf.toString();
            //value = value.replace("\n", "");
            if (!value.equals("")) {
                int nPages = Integer.valueOf(value/*buf.toString()*/).intValue();
                metaData.setPageNum(nPages);
            }
        }
        buf.setLength(0);
    }

    @Override
    public void characters(char ch[], int start, int length) {
        buf.append(ch, start, length);
    }

    @Override
    public void fatalError(SAXParseException e) {

    }

    @Override
    public void error(SAXParseException e) {

    }
}
