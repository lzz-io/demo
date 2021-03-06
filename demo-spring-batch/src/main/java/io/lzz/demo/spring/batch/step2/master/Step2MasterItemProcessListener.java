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
import org.springframework.batch.core.ItemProcessListener;
import org.springframework.stereotype.Component;

import io.lzz.demo.spring.batch.entity.User;

/**
 * @author q1219331697
 *
 */
@Component
public class Step2MasterItemProcessListener implements ItemProcessListener<User, String> {

	private static final Logger log = LoggerFactory.getLogger(Step2MasterItemProcessListener.class);

	@Override
	public void beforeProcess(User item) {
		log.debug("{}", item);
	}

	@Override
	public void afterProcess(User item, String result) {
		log.debug("item:{},result:{}", item, result);
	}

	@Override
	public void onProcessError(User item, Exception e) {
		log.error("{}", item, e);
	}

}
