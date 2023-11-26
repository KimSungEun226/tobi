package springbook.learningtest.template;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Calculator {

	public int calcSum(String path) throws IOException{
		LineCallback<Integer> callback = new LineCallback<Integer>() {
			public Integer doSomethingLine(String line, Integer value) throws IOException {
				return value + Integer.valueOf(line);
			}
		};
		return lineReadTemplate(path, callback, 0);
	}


	public int calcMultiply(String path) throws IOException {
		LineCallback<Integer> callback = new LineCallback<Integer>() {
			public Integer doSomethingLine(String line, Integer value) throws IOException {
				return value * Integer.valueOf(line);
			}
		};
		return lineReadTemplate(path, callback, 1);
	}
	
	public String concatenate(String path) throws IOException {
		LineCallback<String> callback = new LineCallback<String>() {
			public String doSomethingLine(String line, String value) throws IOException {
				return value + line;
			}
		};
		return lineReadTemplate(path, callback, "");
	}
	
	public <T> T lineReadTemplate(String filePath, LineCallback<T> callback, T initVal) throws IOException{
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(filePath));
			T res = initVal;
			String line = null;
			while((line = br.readLine()) != null) res = callback.doSomethingLine(line, res);
			return res;
		}catch (IOException e) {
			System.out.println(e.getMessage());
			throw e;
		}finally {
			if(br != null) {
				try { br.close();}
				catch(IOException e) {e.getMessage();} 
			}
		}
	}

}
