/*
 * Copyright qq:1219331697
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.lzz.demo.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 文件操作类
 * 
 * @author q1219331697
 *
 */
public class FileUtils {

	private static final Logger log = LoggerFactory.getLogger(FileUtils.class);

	public static void read(File name) {
		log.info("{}", name);
		FileInputStream fileInputStream = null;
		try {
			fileInputStream = new FileInputStream(name);
			byte[] data = new byte[4096];
			fileInputStream.read(data);
			log.info(new String(data, "gb2312"));
		} catch (Exception e) {
			log.error("", e);
		} finally {
			try {
				fileInputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void write(File name) throws Exception {
		FileOutputStream fileOutputStream = new FileOutputStream(name);
		String string = "中文abc";
		byte[] bytes = string.getBytes("gbk");
		fileOutputStream.write(bytes);
		// 换行
		fileOutputStream.write("\r\n".getBytes());
		fileOutputStream.close();
	}
}
