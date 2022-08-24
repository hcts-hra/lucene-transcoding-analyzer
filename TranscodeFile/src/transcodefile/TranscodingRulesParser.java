package transcodefile;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.sanskritlibrary.FSM;
import org.sanskritlibrary.ResourceInputStream;
import org.sanskritlibrary.TranscodingRule;
import org.sanskritlibrary.webservice.TransformMap;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class TranscodingRulesParser extends DefaultHandler {

    private Locator locator;
    private List<TranscodingRule> trs = new ArrayList<TranscodingRule>();
    private ResourceInputStream ris;
    private Hashtable<String, FSM[]> fsmh;
    private TransformMap currentTM = null;
    private Hashtable<Character, String> mappings = new Hashtable<Character, String>();
    private String regexName;
    private String regexPattern;

    public TranscodingRulesParser(String filename, ResourceInputStream ris, Hashtable<String, FSM[]> fsmh) throws Exception {
        InputStream is = new FileInputStream(filename);
        this.ris = ris;
        this.fsmh = fsmh;
        SAXParserFactory factory = SAXParserFactory.newInstance();
        try {
            SAXParser saxParser = factory.newSAXParser();
            saxParser.parse(is, this);
        } catch (Throwable t) // probably XML is not well-formed
        {
            try {
                is.close();
            } catch (Exception ex) {
            }        // close the file since SAX won't do it.
            t.printStackTrace();
        }
    }

    public List<TranscodingRule> getTranscodingRules() {
        return trs;
    }

    public boolean validate () {
        boolean rc = true;
        for (TranscodingRule tr : trs) {
            if (tr.tm == null) {
                continue;
            }
            char left = tr.tm.getLeftFlag();
            if (left == '{' || left == '}') {
                rc = false;
                System.out.format("%s: use of { or } not allowed as value of leftFlag\n", id(tr));
            }

            char right = tr.tm.getRightFlag();
            if (right == '{' || right == '}') {
                rc = false;
                System.out.format("%s: use of { or } not allowed as value of rightFlag\n", id(tr));
            }            
        }
        return rc;
    }

    private String id (TranscodingRule tr) {
        if (tr.regexName != null) {
            return String.format("%s:%d", tr.regexName, tr.groupNumber);
        } else {
            return String.format("%s", tr.startTag);
        }
    }

    @Override
    public void setDocumentLocator(Locator locator) {
        this.locator = locator;
    }

    @Override
    public void startDocument() throws SAXException {
    }

    @Override
    public void endDocument() throws SAXException {
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        try {
            if (qName.equals("transcodingRule")) {
                processTranscodingRule(atts);
            } else if (qName.equals("mapping")) {
                processMapping(atts);
            }
            else if (qName.equals("regexp")) {
                processRegexp(atts);
            }
        } catch (Exception ex) {
            throw new SAXException(ex.getMessage());
        }
    }


    private void processTranscodingRule(Attributes atts) throws Exception {
        String startTag = getAttributeValue(atts, "startTag", null);
        String endTag = getAttributeValue(atts, "endTag", null);
        String newStartTag = getAttributeValue(atts, "newStartTag", null);
        String newEndTag = getAttributeValue(atts, "newEndTag", null);
        String groupNumber = getAttributeValue(atts, "groupNumber", null);
        String tradition = getAttributeValue(atts, "tradition", null);
        String traditionStyle = getAttributeValue(atts, "traditionStyle", null);

        String sourceEncoding = getAttributeValue(atts, "sourceEncoding", null);
        if (sourceEncoding == null) {
            System.out.println("sourceEncoding must be specified.\n");
            sourceEncoding = "asis";
        }

        String targetEncoding = getAttributeValue(atts, "targetEncoding", null);
        if (targetEncoding == null) {
            System.out.println("targetEncoding must be specified.\n");
            targetEncoding = "asis";
        }

        char ch = getCharAttribute(atts, "removeTag", 'n');
        boolean removeTag = Character.toLowerCase(ch) == 'y';
        char leftFlag = getCharAttribute(atts, "leftFlag", '[');
        char rightFlag = getCharAttribute(atts, "rightFlag", ']');

        String s = getAttributeValue(atts, "unknown", "flag").toLowerCase();

        FSM.OptionsUnknown unknown = FSM.OptionsUnknown.FLAG;
        if (s.equals("flag")) {
            unknown = FSM.OptionsUnknown.FLAG;
        } else if (s.equals("asis")) {
            unknown = FSM.OptionsUnknown.ASIS;
        } else if (s.equals("ignore")) {
            unknown = FSM.OptionsUnknown.IGNORE;
        } else {
            System.out.format("illegal option (%s) for unknown, using 'flag'\n", s);
        }

        //public enum OptionsFlagged {ASIS_DELIMS, ASIS_NO_DELIMS, IGNORE};

        s = getAttributeValue(atts, "flagged", "asis+");
        FSM.OptionsFlagged flagged = FSM.OptionsFlagged.ASIS_DELIMS;
        if (s.equals("asis+")) {
            flagged = FSM.OptionsFlagged.ASIS_DELIMS;
        } else if (s.equals("asis-")) {
            flagged = FSM.OptionsFlagged.ASIS_NO_DELIMS;
        } else if (s.equals("ignore")) {
            flagged = FSM.OptionsFlagged.IGNORE;
        } else {
            System.out.format("illegal option (%s) for flagged, using 'asis+'\n", s);
        }

        TranscodingRule tr = null;
        if (!"asis".equals(sourceEncoding) && !"asis".equals(targetEncoding)) {
            StringBuilder params = new StringBuilder();
            if (tradition != null) {
                if (traditionStyle == null) {
                    traditionStyle = "1";
                }

                params.append(String.format("[trad:%s,tradStyle:%s]", tradition, traditionStyle));                
            }
            String map = String.format("*:%s->%s%s", sourceEncoding, targetEncoding, params.toString());
            currentTM = new TransformMap(map, ris, fsmh);
            currentTM.setLeftFlag(leftFlag);
            currentTM.setRightFlag(rightFlag);
            currentTM.setRemoveTag(removeTag);
            currentTM.setOptionUnknown(unknown);
            currentTM.setOptionFlagged(flagged);
            tr = new TranscodingRule(startTag, endTag, newStartTag, newEndTag, groupNumber, currentTM);
        } else {
            tr = new TranscodingRule(startTag, endTag, newStartTag, newEndTag, groupNumber, null);
        }

        if (regexName != null) {
            tr.setRegexp(regexName, regexPattern);
        }
        trs.add(tr);
    }


    void processMapping(Attributes atts) {
        String s = getAttributeValue(atts, "char", null);
        if (s == null) {
            System.out.println("mapping must have char attribute. mapping ignored.");
            return;
        }

        String target = getAttributeValue(atts, "target", null);
        if (target == null) {
            System.out.println("mapping must have target attribute. mapping ignored.");
            return;
        }

        mappings.put(s.charAt(0), target);
    }

    void processRegexp(Attributes atts) {
        String name = getAttributeValue(atts, "name", null);
        if (name == null) {
            System.out.println("regexp must have 'name' attribute. regexp ignored.\n");
            return;
        }

        String pattern = getAttributeValue(atts, "pattern", null);
        if (pattern == null) {
            System.out.println("regexp must have a 'pattern' attribute. mapping ignored.\n");
            return;
        }

        regexName = name;
        regexPattern = pattern;
    }

    char getCharAttribute(Attributes atts, String name, char dflt){
        char ch = dflt;
        String temp = getAttributeValue(atts, name, null);
        if (temp != null) {
            if (temp.length() != 1) {
                System.out.format("value of  %s must be a single character", name);
            } else {
                ch = temp.charAt(0);
            }
        }
        return ch;
    }


    String getAttributeValue(Attributes atts, String attName, String dflt){
        String value = atts.getValue(attName);
        if (value != null) {
            return value;
        }
        else {
            return dflt;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equals("mappings")) {
            currentTM.setMappings(mappings);
        } else if (qName.equals("regexp")) {
            regexName = null;
            regexPattern = null;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
    }
}
