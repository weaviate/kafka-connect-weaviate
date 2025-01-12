package io.weaviate.connector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Properties;

public class Version {
    private static final Logger log = LoggerFactory.getLogger(Version.class);
    private static String version = "unknown";

    private static final String VERSION_FILE = "/kafka-connect-weaviate-version.properties";

    static {
        try {
            Properties props = new Properties();
            try (InputStream versionFileStream = Version.class.getResourceAsStream(VERSION_FILE)) {
                props.load(versionFileStream);
                version = props.getProperty("version", version).trim();
            }
        } catch (Exception e) {
            log.error("Error while loading version:", e);
        }
    }

    public static String getVersion() {
        return version;
    }
}
