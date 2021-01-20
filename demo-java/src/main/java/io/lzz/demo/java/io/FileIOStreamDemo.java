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
			// 2.1 一次读一个字节
			// int b; // 一次读一个字节
			// while ((b = inputStream.read()) != -1) {
			// 	outputStream.write(b); // 读到的一个字节输出
			// }

			//	2.2 定义一个缓冲数组
			byte[] buf = new byte[1024];
			int length; // 读到的字节数
			while ((length = inputStream.read(buf)) != -1) {
				outputStream.write(buf, 0, length);
			}

		}
		//	3、关闭

	}
}
