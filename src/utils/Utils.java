package utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class Utils {
	//read a file to list
	public static void readLines(String file, ArrayList<String> lines, String code) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader( new InputStreamReader( new FileInputStream( new File(file)),code));
			String line = null;
			while ((line = reader.readLine()) != null) {
				lines.add(line);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}
	public static <T> Collection<T> intersect(Collection<T> a, Collection<T> b) {
		Set<T> intersection = new HashSet<T>(a);
		intersection.retainAll(b);
		return intersection;    
	}
	/**
	 * transpose of two-dimensional array
	 * 
	 * @param prob 
	 * @return
	 */
	public static double[][] arrayTrans(double[][] prob){
		double[][] pro_new =  new double[prob[0].length][prob.length];
		for(int i = 0; i < prob[0].length; i++){
			for(int j = 0; j < prob.length; j++){
				pro_new[i][j] = prob[j][i];
			}
		}
		return pro_new;
	}
	/**
	 * 
	 * @param file
	 * @param content
	 * @param code
	 * @throws IOException
	 */
	public static void writeFile(String file, String content,String code) throws IOException {

		File fileOutput = new File(file);
		OutputStream out = new FileOutputStream(fileOutput, false);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out, code));
		bw.write(content);
		bw.close();
		out.close();
	}
	// write list to a file
	public static void writeLines(String file, ArrayList<?> counts, String code) {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter( new OutputStreamWriter( new FileOutputStream( new File(file)),code));
			for (int i = 0; i < counts.size(); i++) {
				writer.write(counts.get(i) + "\n");
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}
}
