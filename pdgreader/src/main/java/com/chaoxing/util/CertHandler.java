/*
 * 功能描述：解析证书
 * Author：liujun
 */
package com.chaoxing.util;


import android.text.TextUtils;

import com.chaoxing.bean.BookCert;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public class CertHandler extends DefaultHandler {
    private BookCert cert = new BookCert();
    private StringBuffer buf = new StringBuffer();

    public BookCert getBookCert() {
        return cert;
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
        if (localName.equals("copy")) {
            String value = atts.getValue("cancopy");
            if (value != null && !TextUtils.isEmpty(value))
                cert.setCanCopy(Integer.valueOf(value).intValue());
            value = "";
            value = atts.getValue("copylimit");
            if (value != null)
                cert.setCopyLimit(value);
            value = "";
            value = atts.getValue("copyrange");
            if (value != null)
                cert.setCopyRange(value);
        } else if (localName.equals("print")) {
            String value = atts.getValue("canprint");
            if (value != null && !TextUtils.isEmpty(value))
                cert.setCanPrint(Integer.valueOf(value).intValue());
            value = "";
            value = atts.getValue("printlimit");
            if (value != null)
                cert.setPrintLimit(value);
            value = "";
            value = atts.getValue("printrange");
            if (value != null)
                cert.setPrintRange(value);
        }
    }

    @Override
    public void endElement(String namespaceURI, String localName,
                           String qName) throws SAXException {
        if (localName.equals("certexpdate")) {
            cert.setCertExpdate(buf.toString());
        } else if (localName.equals("username")) {
            cert.setUserName(buf.toString());
        } else if (localName.equals("password")) {
            cert.setPassword(buf.toString());
        } else if (localName.equals("useraccount")) {
            cert.setUserAccount(buf.toString());
        } else if (localName.equals("userexpdate")) {
            cert.setUserExpdate(buf.toString());
        } else if (localName.equals("bookkey")) {
            cert.setBookKey(buf.toString());
        } else if (localName.equals("auth")) {
            cert.setAuth(buf.toString());
        } else if (localName.equals("reserve")) {
            cert.setReserve(buf.toString());
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
