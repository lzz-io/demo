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

package io.lzz.demo.spring.test.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

/**
 * @author q1219331697
 *
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@AutoConfigureMockMvc
public class IndexControllerTest {

	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(IndexControllerTest.class);

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private IndexController indexController;

	public void testIndex() throws Exception {
		mockMvc.perform(get("/user"))//
				.andExpect(status().isOk());
		indexController.index();
	}

	@Test
	public void testShow() {
		// indexController.show(1);
	}

	@Test
	public void testEditNew() {
	}

	@Test
	public void testCreate() {
	}

	@Test
	public void testEdit() {
	}

	@Test
	public void testUpdate() {
	}

	@Test
	public void testDestroy() {
	}

}
