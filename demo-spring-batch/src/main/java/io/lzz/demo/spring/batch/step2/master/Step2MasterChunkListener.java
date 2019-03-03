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

package io.lzz.demo.spring.batch.step2.master;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.stereotype.Component;

/**
 * @author q1219331697
 *
 */
@Component
public class Step2MasterChunkListener implements ChunkListener {

	private static final Logger log = LoggerFactory.getLogger(Step2MasterChunkListener.class);

	@Override
	public void beforeChunk(ChunkContext context) {
		log.debug("{}", context);
	}

	@Override
	public void afterChunk(ChunkContext context) {
		log.debug("{}", context);
	}

	@Override
	public void afterChunkError(ChunkContext context) {
		log.error("{}", context);
		String[] attributeNames = context.attributeNames();
		for (String string : attributeNames) {
			log.error("{}:{}", string, context.getAttribute(string));
		}
	}

}
