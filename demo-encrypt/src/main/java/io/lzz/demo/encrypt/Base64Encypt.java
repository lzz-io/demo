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

package io.lzz.demo.encrypt;

import java.util.HashMap;
import java.util.Map;

import org.springframework.util.Base64Utils;

/**
 * @author q1219331697
 *
 */
public class Base64Encypt {

	public static void main(String[] args) throws Exception {
		String string = "中文";
		byte[] src = string.getBytes("gb2312");
		String encodeToString = Base64Utils.encodeToString(src);

		Map<String, Object> map = new HashMap<>();
		map.put("key", encodeToString);

		String value = map.get("key").toString();
		byte[] decodeFromString = Base64Utils.decode(value.getBytes("GB18030"));
		String string2 = new String(decodeFromString, "GB18030");
		System.out.println(string2);
	}
}
