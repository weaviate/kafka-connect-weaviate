package io.weaviate.connector;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.sink.SinkConnector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WeaviateSinkConnector extends SinkConnector {
    private Map<String, String> configProps;

    @Override
    public void start(Map<String, String> map) {
        configProps = map;
    }

    @Override
    public Class<? extends Task> taskClass() {
        return WeaviateSinkTask.class;
    }

    @Override
    public List<Map<String, String>> taskConfigs(int maxTask) {
        ArrayList<Map<String, String>> tasks = new ArrayList<>();
        for (int i = 0; i < maxTask; i++) {
            tasks.add(configProps);
        }
        return tasks;
    }

    @Override
    public void stop() {

    }

    @Override
    public ConfigDef config() {
        return WeaviateSinkConfig.CONFIG_DEF;
    }

    @Override
    public String version() {
        return getClass().getPackage().getImplementationVersion();
    }


}
