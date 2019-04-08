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

package io.lzz.demo.spring.batch.config.reader;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.PassThroughLineMapper;
import org.springframework.core.io.FileSystemResource;

/**
 * PassThroughReader
 * 
 * 通过LineMapper直接传递原始字符串，而不是映射对象。
 * 
 * @author q1219331697
 *
 */
public class PassThroughReader {

	// reader
	public ItemReader<String> reader() {
		FlatFileItemReader<String> reader = new FlatFileItemReader<>();
		reader.setResource(new FileSystemResource("./tmp/input.csv"));
		reader.setEncoding("GBK");
		reader.setName("reader");

		// 通过LineMapper直接传递原始字符串，而不是映射对象。
		LineMapper<String> lineMapper = new PassThroughLineMapper();
		reader.setLineMapper(lineMapper);

		return reader;
	}
}
