/*
 * Copyright © 2025 Damien Gasparina (damien@gasparina.cloud)
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
        return Version.getVersion();
    }
}
