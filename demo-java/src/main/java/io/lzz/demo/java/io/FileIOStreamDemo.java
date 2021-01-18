package io.lzz.demo.java.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

public class FileIOStreamDemo {

	public static void main(String[] args) throws IOException {

		//	1、创建stream
		// URL url = Thread.currentThread().getContextClassLoader().getResource("./");
		URL url = ClassLoader.getSystemResource("./");
		String path = url.getPath();
		File inFile = new File(path, "input.txt");
		File outFile = new File(path, "output.txt");
		try (
				FileInputStream inputStream = new FileInputStream(inFile);
				FileOutputStream outputStream = new FileOutputStream(outFile)
		) {
			//	2、使用
			int b; // 一次读一个字节
			while ((b = inputStream.read()) != -1) {
				outputStream.write(b); // 读到的一个字节输出
			}
		}
		//	3、关闭

	}
}
