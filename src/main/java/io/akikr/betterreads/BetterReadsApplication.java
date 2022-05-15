package io.akikr.betterreads;

import io.akikr.betterreads.db.connection.DataStaxAstraProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import java.io.File;
import java.nio.file.Paths;

/**
 * @apiNote Main class for BetterReadsApplication
 * @author ankit
 * @since 1.0
 */

@Slf4j
@SpringBootApplication
@EnableConfigurationProperties(DataStaxAstraProperties.class)
public class BetterReadsApplication
{
    /**
     * @apiNote The 'CONFIG_FILE_LOCATION' value for 'spring.config.location' is used here to externalize the spring configurations
     */
    private static final String CONFIG_FILE_LOCATION = "/opt/akikr/config/better-reads-application/better-reads-application.yml";


    // A static block to check and load the application config properties from specified location
    static
    {
        log.info("Loading application config properties...");

        // If file exists then load the specified properties otherwise load default properties
        File file = Paths.get(CONFIG_FILE_LOCATION).toFile();
        if (file.exists())
        {
            log.info("Loading properties from location: " + CONFIG_FILE_LOCATION);
            System.setProperty("spring.config.location", CONFIG_FILE_LOCATION);
        }
        else
        {
            log.info("File doesn't exists at location: " + CONFIG_FILE_LOCATION);
            log.info("Loading default application config properties...");
        }
    }

    public static void main(String[] args)
    {
        SpringApplication.run(BetterReadsApplication.class, args);
    }
}
