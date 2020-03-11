/**
 * Copyright © airback
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.airback.configuration;

import com.airback.core.airbackException;
import com.airback.core.utils.FileUtils;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.properties.EncryptableProperties;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * This file contains all constants define in system properties file
 * application.properties read at system started.
 *
 * @author airback Ltd.
 * @since 1.0
 */
public class ApplicationProperties {
    private static final String RESOURCE_PROPERTIES = "application.properties";
    private static final String DECRYPT_PASS = "airback321";

    private static Properties properties;

    public static final String BI_ENDECRYPT_PASSWORD = "endecryptPassword";

    public static final String DEFAULT_LOCALE = "defaultLocale";

    public static void loadProps() {
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword(DECRYPT_PASS);

        properties = new EncryptableProperties(encryptor);
        try {
            File configFile = getAppConfigFile();

            if (configFile != null) {
                try (InputStreamReader isr = new InputStreamReader(new FileInputStream(configFile), "UTF-8")) {
                    properties.load(isr);
                }
            } else {
                InputStream propStreams = Thread.currentThread().getContextClassLoader().getResourceAsStream(RESOURCE_PROPERTIES);
                if (propStreams == null) {
                    // Probably we are running testing
                    InputStream propStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("default-airback-test.properties");
                    if (propStream != null) {
                        try (InputStreamReader isr = new InputStreamReader(propStream, "UTF-8")) {
                            properties.load(isr);
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new airbackException(e);
        }
    }

    public static File getAppConfigFile() {
        return FileUtils.getDesireFile(FileUtils.getUserFolder(), "config/airback.properties", "src/main/config/airback.properties");
    }

    public static Properties getAppProperties() {
        return properties;
    }

    public static String getString(String key) {
        return getString(key, "");
    }

    public static String getString(String key, String defaultValue) {
        if (properties == null) {
            return defaultValue;
        }
        return properties.getProperty(key, defaultValue);
    }
}
