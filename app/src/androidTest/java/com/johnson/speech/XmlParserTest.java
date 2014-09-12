package com.johnson.speech;

import junit.framework.TestCase;

public class XmlParserTest extends TestCase {

    public void testGetParsedTime() throws Exception {
        String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "    <nlp>\n" +
                "    <version>1.0.0.8000</version>\n" +
                "    <rawtext>晚上七点起床。</rawtext>\n" +
                "    <parsedtext>晚上 七 点 起床</parsedtext>\n" +
                "    <result>\n" +
                "    <focus>schedule</focus>\n" +
                "    <action>\n" +
                "    <operation>create</operation>\n" +
                "    </action>\n" +
                "    <object>\n" +
                "    <datetime>\n" +
                "    <date>2014-09-13</date>\n" +
                "    <time>10:12:23</time>\n" +
                "    <time_orig>晚上七点</time_orig>\n" +
                "    </datetime>\n" +
                "    <name>clock</name>\n" +
                "    </object>\n" +
                "    <content>起床。</content>\n" +
                "    </result>\n" +
                "    </nlp>";
        XmlParser xmlParser = new XmlParser(xml);
        assertEquals(true, xmlParser.getResult());
        assertEquals(10, xmlParser.getHour());
        assertEquals(12, xmlParser.getMinute());
        assertEquals(23, xmlParser.getSecond());
    }
}