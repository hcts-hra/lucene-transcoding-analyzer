package transcodefile;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import org.sanskritlibrary.FSM;
import org.sanskritlibrary.IOAssistance;
import org.sanskritlibrary.ResourceInputStream;
import org.sanskritlibrary.TextSegment;
import org.sanskritlibrary.TranscodeFile;
import org.sanskritlibrary.TranscodingRule;
/*
 * -dir:transcoding directory
 * -rules:transcoding rules
 * -doc:
 * -help
 */
public class Main implements ResourceInputStream {
    Hashtable<String, FSM[]> fsmh = new Hashtable<String, FSM[]>();
    boolean removeTag = false;
    String transcodingDirectory = null;
    private static String helpText = "java -jar TranscodeFile.jar [-in:<document>] [-out:<transcoded document>] [-rules:<transcoding rules>] [-d<transcoder directory>] [-re:<regexName> [-chars:<charlistdirectory>] [-help]\n" +
            "where\n" +
            "<document> is name of file to transcode. Defaults to doc.txt\n" +
            "<transcoded document> is name of transcoded file. Default to the name of the input file preceded by an underscore(_)\n" +
            "<transcoding rules> is name of file that contains transcoding rules. Defaults to tr.xml\n" +
            "<transcoder directory> is name of directory that contains transcoder mapping files. Defaults to transcoder\n" +
            "<regexName> is name of regular expression to use in the transcoding rules file\n" +
            "<charlistdirectory> is name of that contains files to collect data on" +
            "See tr.xml and TranscodeFile.txt for more information.\n";
    public static void main(String[] args) throws Exception {
        new Main(args);
    }

    public Main(String[] args) throws Exception {
        Hashtable<String, String> h = new Hashtable<String, String>();
        h.put("-in", "doc.txt");
        h.put("-out", "");
        h.put("-rules", "tr.xml");
        h.put("-dir", "transcoders");
        h.put("-report", "");
        h.put("-re", "");
        h.put("-chars", "");
        h.put("-generate", "");             // generate a summary of the given transcoding file


        if (!parseCommandLine(h, args)) {
            System.out.println("Illegal command line:");
            for (String arg : args) {
                System.out.println(arg);
            }
            System.out.println("-------------------------");
            System.out.format("-in:%s\n", h.get("-in"));
            System.out.format("-out:%s\n", h.get("-out"));
            System.out.format("-rules:%s\n", h.get("-rules"));
            System.out.format("-dir:%s\n", h.get("-dir"));
            System.out.println("-------------------------");
            System.out.println(helpText);
            return;
        }

        if (h.get("generate") != null) {
            generate(h.get("generate"));
        }

        if (h.get("-help") != null) {
            System.out.println(helpText);
            return;
        }

        if (h.get("-chars").length() > 0) {
            summarizeFiles(h.get("-chars"));
            return;
        }

        String in = h.get("-in");
        String out = h.get("-out");
        String transcodingFile = h.get("-rules");
        transcodingDirectory = h.get("-dir");
        StringBuilder sb = new StringBuilder();
        if (!new File(in).exists()) {
            sb.append(String.format("document file: %s does not exist\n", in));
        }

        if (!new File(transcodingFile).exists()) {
            sb.append(String.format("transcoding rules file: %s does not exist\n", transcodingFile));
        }

        if (!new File(transcodingDirectory).exists()) {
            sb.append(String.format("Directory %s does not exists\n", transcodingDirectory));
        }

        if (sb.length() > 0) {
            System.out.format("%s\n%s", sb.toString(), helpText);
            return;
        }

        if (out.length() == 0) {
            String filename = new File(in).getName();
            int len = in.length() - filename.length();
            if (len > 0) {
                out = in.substring(0, len);
                if (!out.endsWith(File.separator)) {
                    out += File.separator;
                }
            }
            out += "_" + filename;
            h.put("-out", out);
        }

        if (h.get("-report").length()>0) {
            printReport(h);
        }

        doTranscoding(in, out, transcodingFile, h);
    }


    private void generate(String transcodingFile) {
        StringBuilder sb = new StringBuilder();
        for (int i=1; i<256; i++) {
            sb.append(String.format("%c\n", (char)i));
        }
    }

    private void summarizeFiles(String directory) throws Exception {
        Hashtable<Character, Hashtable<String, Integer>> h = new Hashtable<Character, Hashtable<String, Integer>>();
        File[] files = new File(directory).listFiles();
        for (File file: files) {
            String chars = IOAssistance.readEntireFile(file.getAbsolutePath());
            int len = chars.length();
            for (int i=0; i<len; i++) {
                char ch = chars.charAt(i);
                if (FSM.slpVowel.get(ch) != null || FSM.slpCharacter.get(ch) != null) {
                    continue;
                }

                Hashtable<String, Integer> hh = h.get(ch);
                if (hh == null) {
                    hh = new Hashtable<String, Integer>();
                    h.put(ch, hh);
                }
                Integer count = hh.get(file.getName());
                if (count == null) {
                    count = new Integer(1);
                } else {
                    count = new Integer(count.intValue() + 1);
                }
                hh.put(file.getName(), count);
            }
        }

        ArrayList<Character> a = new ArrayList<Character>();
        Enumeration<Character> en = h.keys();
        while (en.hasMoreElements()) {
            a.add(en.nextElement());
        }
        Collections.sort(a);
        BufferedWriter bw = IOAssistance.openWriter("chars.txt");
        for (Object ch : a) {
            bw.write((Character)ch);
            bw.write("\n");
            Hashtable<String, Integer> hh = h.get((Character)ch);
            ArrayList<String> aa = new ArrayList<String>();
            Enumeration<String> ens = hh.keys();
            while (ens.hasMoreElements()) {
                aa.add(ens.nextElement());
            }
            Collections.sort(aa);
            for (String fn : aa) {
                bw.write(String.format("%s (%d)\n", fn, hh.get(fn)));
            }
            bw.write("\n");
        }
        bw.close();
    }

    private void printReport(Hashtable<String, String> h) {
        File f = new File(h.get("-in"));
        System.out.format("input file: %s\n", f.getPath());

        f = new File(h.get("-out"));
        System.out.format("output file: %s\n", f.getPath());

        f = new File(h.get("-dir"));
        System.out.format("transcoding directory: %s\n", f.getPath());

        if (h.get("-re").length() == 0) {
            System.out.println("No regular expression. Use startTag and endTag");
        } else {
            System.out.format("Use regular expression named '%s' instead of startTag and endTag", h.get("-re"));
        }

        System.out.println("Produce this report (-report)");

        f = new File(h.get("-rules"));
        System.out.format("rules file: %s\n", f.getPath());

        try {
            BufferedReader br = IOAssistance.openReader(f.getPath());
            String line = null;
            while ((line = br.readLine())!= null) {
                line = line.replace("&lt;", "<");
                line = line.replace("&gt;", ">");
                System.out.format("    %s\n", line);
            }
            br.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

    }

    private boolean parseCommandLine(Hashtable<String, String> h, String[] args) {
        for (String arg : args) {
            if (arg.toLowerCase().equals("-help")) {
                h.put("-help", "yes");
                return true;
            }
            else if (arg.toLowerCase().equals("-report")) {
                h.put("-report", "yes");
            }
            else {
                int idx = arg.indexOf(":");
                if (idx == -1) {
                    return false;
                }

                String command = arg.substring(0, idx).toLowerCase();
                if (h.get(command) == null) {
                    return false;
                } else {
                    h.put(command, arg.substring(idx+1));
                }
            }
        }

        return true;
    }

    private void doTranscoding(String in, String out, String transcodingFile, Hashtable<String, String> options) {
        TranscodeFile tf = null;
        Hashtable<String, Object> h = new Hashtable<String, Object>();
        List<TextSegment> segments = null;
        String text = null;
        List<TranscodingRule> trs = null;
        String regexName = options.get("-re");
        String pattern = null;

        try {
            TranscodingRulesParser trp = new TranscodingRulesParser(transcodingFile, this, fsmh);
            if (!trp.validate()) {          // errors already written
                System.out.println("Transcoding not done because of above errors.");
                return;
            }

            trs = trp.getTranscodingRules();

            tf = new TranscodeFile();
            text = IOAssistance.readEntireFile(in);
            segments = tf.findSegments(h, trs, text, regexName);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return;
        }

        String result = tf.doEncodings(h, text, segments, in, trs);
        BufferedWriter bw = null;
        try {
            bw = IOAssistance.openWriter(out);
            bw.write(result);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        finally {
            if (bw != null) {
                try {bw.close();} catch (Exception ex){}
            }
        }
    }



    public InputStream[] getInputStreams(String sourceEncoding, String targetEncoding)
    {
        String[] fileNames = FSM.createFileNames("", sourceEncoding, targetEncoding);
        InputStream[] iss = new InputStream[fileNames.length];
        int i = 0;

        for (String name : fileNames)
        {
            String fname = String.format("%s/%s", transcodingDirectory, name);
            try
            {
                iss[i++] = new FileInputStream(fname);
            } catch (Exception ex)
            {
            }
        }

        return iss;
    }
}
