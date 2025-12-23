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

package io.lzz.demo.spring.batch.component;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;

import java.util.HashMap;
import java.util.Map;
import java.util.function.IntFunction;

/**
 * @author q1219331697
 */
@Getter
@Setter
@Accessors(chain = true)
public class BasicPartitioner implements Partitioner {

    private static final String DEFAULT_KEY_NAME = "gridIndex";
    private static final String PARTITION_KEY = "partition";
    private IntFunction<Object> function = i -> i;
    private String keyName = DEFAULT_KEY_NAME;

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        Map<String, ExecutionContext> map = new HashMap<>(gridSize);
        for (int i = 0; i < gridSize; i++) {
            ExecutionContext executionContext = new ExecutionContext();
            executionContext.put(keyName, function.apply(i));
            map.put(PARTITION_KEY + i, executionContext);
        }
        return map;
    }

}
