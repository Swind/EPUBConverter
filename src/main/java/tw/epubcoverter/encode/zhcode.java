package tw.epubcoverter.encode;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.Hashtable;
import java.util.Vector;

/* Copyright 2002 Erik Peterson 
 Code and program free for non-commercial use.
 Contact erik@mandarintools.com for fees and
 licenses for commercial use.
 */

public class zhcode extends Encoding {
	// Simplfied/Traditional character equivalence hashes
	protected Hashtable s2thash, t2shash;
	private SinoDetect sinodetector;
	public boolean autodetect;
	private int unsupportedStrategy;

	public final static int DELETE = 0;
	public final static int HTMLESC = 1;
	public final static int UNIESC = 2;
	public final static int QUESTIONMARK = 3;
	public final static int TOTAL = 4;

	public final static String[] strategyNames = { "Delete", "HTML Escape",
			"Unicode Escape", "Question Mark" };

	public final static String[] strategyDescriptions = { "Delete",
			"Use HTML Escape (e.g. &#x4e00;)",
			"Use Unicode Escape (e.g. \\u4e00)", "Replace with Question Mark" };

	// Constructor
	public zhcode() {
		super();
		unsupportedStrategy = UNIESC;
		String dataline;
		sinodetector = new SinoDetect();
		autodetect = false;

		// Initialize and load in the simplified/traditional character hashses
		s2thash = new Hashtable();
		t2shash = new Hashtable();

		try {
//			InputStream pydata = getClass().getResourceAsStream("hcutf8.txt");
			InputStream pydata = new BufferedInputStream(new FileInputStream("hcutf8.txt"));
			BufferedReader in = new BufferedReader(new InputStreamReader(
					pydata, "UTF8"));
			while ((dataline = in.readLine()) != null) {
				// Skip empty and commented lines
				if (dataline.length() == 0 || dataline.charAt(0) == '#') {
					continue;
				}

				// Simplified to Traditional, (one to many, but pick only one)
				s2thash.put(dataline.substring(0, 1), dataline.substring(1, 2));

				// Traditional to Simplified, (many to one)
				for (int i = 1; i < dataline.length(); i++) {
					t2shash.put(dataline.substring(i, i + 1),
							dataline.substring(0, 1));
				}
			}
		} catch (Exception e) {
			System.err.println(e);
		}

	}

	public void setUnsupportedStrategy(int strategy) {
		if (strategy >= 0 && strategy < TOTAL) {
			unsupportedStrategy = strategy;
		}
	}

	public int getUnsupportedStrategy() {
		return unsupportedStrategy;
	}

	public String convertString(String inline, int source_encoding,
			int target_encoding) {
		StringBuffer outline = new StringBuffer(inline);
		convertStringBuffer(outline, source_encoding, target_encoding);
		return outline.toString();
	}

	public void convertStringBuffer(StringBuffer dataline, int source_encoding,
			int target_encoding) {
		int lineindex;
		String currchar;
		char charvalue;

		for (lineindex = 0; lineindex < dataline.length(); lineindex++) {
			charvalue = dataline.charAt(lineindex);
			currchar = "" + charvalue;
			if (((int) charvalue == 0xfeff || (int) charvalue == 0xfffe)
					&& (target_encoding != UNICODE
							&& target_encoding != UNICODES
							&& target_encoding != UNICODET
							&& target_encoding != UTF8
							&& target_encoding != UTF8S && target_encoding != UTF8T)) {
				dataline.deleteCharAt(lineindex);
				continue;
			}

			if ((source_encoding == GB2312 || source_encoding == GBK
					|| source_encoding == ISO2022CN_GB || source_encoding == HZ
					|| source_encoding == GB18030 || source_encoding == UNICODE
					|| source_encoding == UNICODES || source_encoding == UTF8 || source_encoding == UTF8S)
					&& (target_encoding == BIG5 || target_encoding == CNS11643
							|| target_encoding == UNICODET
							|| target_encoding == UTF8T || target_encoding == ISO2022CN_CNS)) {
				if (s2thash.containsKey(currchar) == true) {
					dataline.replace(lineindex, lineindex + 1,
							(String) s2thash.get(currchar));
				}
			} else if ((source_encoding == BIG5 || source_encoding == CNS11643
					|| source_encoding == UNICODET || source_encoding == UTF8
					|| source_encoding == UTF8T
					|| source_encoding == ISO2022CN_CNS
					|| source_encoding == GBK || source_encoding == GB18030 || source_encoding == UNICODE)
					&& (target_encoding == GB2312
							|| target_encoding == UNICODES
							|| target_encoding == ISO2022CN_GB
							|| target_encoding == UTF8S || target_encoding == HZ)) {
				if (t2shash.containsKey(currchar) == true) {
					dataline.replace(lineindex, lineindex + 1,
							(String) t2shash.get(currchar));
				}
			}
		}

		Charset charset = Charset.forName(javaname[target_encoding]);
		CharsetEncoder encoder = charset.newEncoder();

		for (int i = 0; i < dataline.length(); i++) {
			if (encoder.canEncode(dataline.subSequence(i, i + 1)) == false) {
				// Replace or delete
				// Delete
				if (unsupportedStrategy == DELETE) {
					dataline.deleteCharAt(i);
					i--;
				} else if (unsupportedStrategy == HTMLESC) {
					// HTML Escape &#xNNNN;
					dataline.replace(
							i,
							i + 1,
							"&#x"
									+ Integer.toHexString((int) dataline
											.charAt(i)) + ";");
				} else if (unsupportedStrategy == UNIESC) {
					// Unicode Escape \\uNNNN
					dataline.replace(
							i,
							i + 1,
							"\\u"
									+ Integer.toHexString((int) dataline
											.charAt(i)));
				} else if (unsupportedStrategy == QUESTIONMARK) {
					// Unicode Escape \\uNNNN
					dataline.replace(i, i + 1, "?");
				}
			}
		}

	}

	public File convertFile(String sourcedir, String targetdir,
			int source_encoding, int target_encoding) {
		BufferedReader srcbuffer;
		BufferedWriter outbuffer;
		String dataline;

		Vector inputfiles = new Vector();
		Vector outputfiles = new Vector();
		inputfiles.add(sourcedir);
		outputfiles.add(targetdir);
		int i, j, working_encoding;
		File tmpfile, tmpout;
		String dirfiles[];
		for (i = 0; i < inputfiles.size(); i++) {
			tmpfile = new File((String) inputfiles.get(i));
			if (tmpfile.exists() == false) {
				System.out.println("ERROR: Source file "
						+ (String) inputfiles.get(i) + " does not exist.\n");
				continue;
			}
			if (tmpfile.isDirectory() == true) {
				tmpout = new File((String) outputfiles.get(i));
				if (tmpout.exists() == false) {
					tmpout.mkdir();
				}

				dirfiles = tmpfile.list();
				if (dirfiles != null) {
					for (j = 0; j < dirfiles.length; j++) {
						inputfiles.add((String) inputfiles.get(i)
								+ File.separator + dirfiles[j]);
						outputfiles.add((String) outputfiles.get(i)
								+ File.separator + dirfiles[j]);

					}
				}
				continue;
			}

//			System.err.println("Converting " + inputfiles.get(i) + " to "
//					+ outputfiles.get(i) + " with encoding " + source_encoding);
			try {
				int codeguess = sinodetector.detectEncoding(tmpfile);
				if (codeguess == Encoding.ASCII || codeguess == Encoding.OTHER) {
					// Just copy file as is
					InputStream in = new FileInputStream(new File(
							(String) inputfiles.get(i)));
					OutputStream out = new FileOutputStream(new File(
							(String) outputfiles.get(i)));

					// Transfer bytes from in to out
					byte[] buf = new byte[1024];
					int len;
					while ((len = in.read(buf)) > 0) {
						out.write(buf, 0, len);
					}
					in.close();
					out.close();
					continue;
				}

				working_encoding = source_encoding;
//				if (autodetect) {
					working_encoding = codeguess;
//				}
				srcbuffer = new BufferedReader(new InputStreamReader(
						new FileInputStream((String) inputfiles.get(i)),
						javaname[working_encoding]));
				outbuffer = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream((String) outputfiles.get(i)),
						javaname[target_encoding]));
				while ((dataline = srcbuffer.readLine()) != null) {
					outbuffer.write(convertString(dataline, working_encoding,
							target_encoding));
					outbuffer.newLine();
				}
				srcbuffer.close();
				outbuffer.close();
			} catch (Exception ex) {
				System.err.println(ex);
			}

		}

		return new File(targetdir);

	}

}
