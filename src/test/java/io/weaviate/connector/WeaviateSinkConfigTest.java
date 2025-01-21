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

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class WeaviateSinkConfigTest {
    @Test
    void ensureHeaderParsing() {
        HashMap<String, String> originals = new HashMap<>() {{
            put(WeaviateSinkConfig.HEADERS_CONFIG, "X-OpenAI-Api-Key=XYZ");
        }};

        WeaviateSinkConfig config = new WeaviateSinkConfig(WeaviateSinkConfig.CONFIG_DEF, originals);
        Map<String, String> headers = config.getHeaders();

        assertTrue(headers.containsKey("X-OpenAI-Api-Key"));
        assertEquals("XYZ", headers.get("X-OpenAI-Api-Key"));
    }


    @Test
    void ensureInvaliHeaderAreThrowingException() {
        HashMap<String, String> originals = new HashMap<>() {{
            put(WeaviateSinkConfig.HEADERS_CONFIG, "X-OpenAI-Api-Key/XYZ"); // Invalid Header format
        }};

        assertThrows(IllegalArgumentException.class, () -> new WeaviateSinkConfig(WeaviateSinkConfig.CONFIG_DEF, originals));
    }

    @Test
    void ensureDeleteEnabledEnforceKafkaId() {
        HashMap<String, String> originals = new HashMap<>() {{
            put(WeaviateSinkConfig.DELETE_ENABLED_CONFIG, "true");
        }};

        assertThrows(IllegalArgumentException.class, () -> new WeaviateSinkConfig(WeaviateSinkConfig.CONFIG_DEF, originals));
    }

}