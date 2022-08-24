package de.unihd.hra.libs.java.luceneTranscodingAnalyzer;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Hashtable;

import org.apache.logging.log4j.LogManager;
import org.sanskritlibrary.FSM;
import org.sanskritlibrary.ResourceInputStream;
import org.sanskritlibrary.webservice.TransformMap;

public class TransformMaps {

	public static Hashtable<String, FSM[]> fsmh = new Hashtable<String, FSM[]>();
	public static HashMap<String, TransformMap> transformMaps = new HashMap<String, TransformMap>();
	private final static LogManager LogManager = LogManager.getLogger(TransformMaps.class);

	public static class Transcoders implements ResourceInputStream {

		public Transcoders() {
		}

		public InputStream[] getInputStreams(String sourceEncoding, String targetEncoding) {
			String[] fileNames = FSM.createFileNames("", sourceEncoding, targetEncoding);
			InputStream[] iss = new InputStream[fileNames.length];
			int i = 0;

			for (String name : fileNames) {
				try {
					iss[i++] = getClass().getResourceAsStream("transcoders" + name);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			return iss;
		}
	}

}
