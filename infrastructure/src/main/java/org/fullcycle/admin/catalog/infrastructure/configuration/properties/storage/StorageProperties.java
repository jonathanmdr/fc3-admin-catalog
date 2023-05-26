package org.fullcycle.admin.catalog.infrastructure.configuration.properties.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

public class StorageProperties implements InitializingBean {

    private static final Logger log = LoggerFactory.getLogger(StorageProperties.class);

    private String fileNamePattern;
    private String locationPattern;

    public String getFileNamePattern() {
        return fileNamePattern;
    }

    public void setFileNamePattern(final String fileNamePattern) {
        this.fileNamePattern = fileNamePattern;
    }

    public String getLocationPattern() {
        return locationPattern;
    }

    public void setLocationPattern(final String locationPattern) {
        this.locationPattern = locationPattern;
    }

    @Override
    public void afterPropertiesSet() {
        log.debug(toString());
    }

    @Override
    public String toString() {
        return "StorageProperties{" +
            "fileNamePattern='" + fileNamePattern + '\'' +
            ", locationPattern='" + locationPattern + '\'' +
            '}';
    }

}
