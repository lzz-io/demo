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

package io.lzz.demo.spring.batch.step3.worker;

import io.lzz.demo.spring.batch.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

/**
 * @author q1219331697
 */
public class Step3WorkerItemProcessor implements ItemProcessor<User, User> {

    private static final Logger log = LoggerFactory.getLogger(Step3WorkerItemProcessor.class);

    @Override
    public User process(User user) throws Exception {
        user.setUserName(user.getUserName() + "修改step3");
        log.debug("{}", user);
        return user;
    }

}
