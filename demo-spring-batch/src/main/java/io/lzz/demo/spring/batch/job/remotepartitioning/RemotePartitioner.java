package io.lzz.demo.spring.batch.job.remotepartitioning;

import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;

import java.util.HashMap;
import java.util.Map;


public class RemotePartitioner implements Partitioner {

    private static final String PARTITION_KEY = "partition";

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        Map<String, ExecutionContext> map = new HashMap<>(gridSize);
        for (int i = 0; i < gridSize; i++) {
            ExecutionContext executionContext = new ExecutionContext();
            executionContext.putInt("mod", i);
            map.put(PARTITION_KEY + i, executionContext);
        }
        return map;
    }

}
