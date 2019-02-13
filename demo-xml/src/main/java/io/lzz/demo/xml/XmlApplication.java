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

package io.lzz.demo.xml;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

/**
 * @author q1219331697
 *
 */
@SpringBootApplication
public class XmlApplication implements CommandLineRunner {

	private static final Logger log = LoggerFactory.getLogger(XmlApplication.class);

	public static void main(String[] args) throws Exception {
		SpringApplication.run(XmlApplication.class, args);
		log.info("application started");
	}

	@Override
	public void run(String... args) throws Exception {
		URL resource = ClassLoader.getSystemResource("data.xml");
		File file = new File(resource.getFile());

		SAXReader saxReader = new SAXReader();
		saxReader.setEncoding("UTF-8");
		Document document = saxReader.read(file);

		@SuppressWarnings("unchecked")
		List<Element> levev2s = document.selectNodes("//level2");
		log.debug("{}", levev2s);
		for (Element element : levev2s) {
			log.debug("{}", element);
		}

		XmlMapper xmlMapper = new XmlMapper();
		// Map<?, ?> entries = xmlMapper.readValue(document.asXML(), Map.class);
		List<?> entries = xmlMapper.readValue(document.asXML(), List.class);
		ObjectMapper jsonMapper = new ObjectMapper();
		String json = jsonMapper.writeValueAsString(entries);
		System.out.println(json);

	}
}
