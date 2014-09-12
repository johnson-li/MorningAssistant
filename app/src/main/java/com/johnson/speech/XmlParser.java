package com.johnson.speech;

import com.johnson.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by johnson on 9/12/14.
 */
public class XmlParser {
    Matcher matcher;
    boolean match = false;

    public XmlParser(String xml) {
        String time = getTime(xml);
        if (time == null) return;
        matcher = getParsedTime(time);
    }

    @Deprecated
    public XmlParser() {

    }

    String getTime(String str) {
        try {
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            StringReader stringReader = new StringReader(str);
            InputSource inputSource = new InputSource(stringReader);
            Document document = documentBuilder.parse(inputSource);
            Element root = document.getDocumentElement();
            Element result = (Element)root.getElementsByTagName("result").item(0);
            Element object = (Element)result.getElementsByTagName("object").item(0);
            Element datetime = (Element)object.getElementsByTagName("datetime").item(0);
            Element time = (Element)datetime.getElementsByTagName("time").item(0);

            return time.getTextContent();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    Matcher getParsedTime(String str) {
        Pattern pattern = Pattern.compile("([01]\\d|2[0-3]):([0-5]\\d):([0-5]\\d)");
        Matcher matcher = pattern.matcher(str);
        if (matcher.matches()) {
            match = true;
            return matcher;
        }
        match = false;
        Log.e("time parse error: " + str);
        return null;
    }

    public int getHour() {
        return match ? Integer.valueOf(matcher.group(1)) : -1;
    }

    public int getMinute() {
        return match ? Integer.valueOf(matcher.group(2)) : -1;
    }

    public int getSecond() {
        return match ? Integer.valueOf(matcher.group(3)) : -1;
    }

    public boolean getResult() {
        return match;
    }
}
