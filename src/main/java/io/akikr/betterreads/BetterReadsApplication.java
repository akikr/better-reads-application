package io.akikr.betterreads;

import io.akikr.betterreads.connection.DataStaxAstraProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * @author ankit
 * @since 1.0
 */
@SpringBootApplication
@EnableConfigurationProperties(DataStaxAstraProperties.class)
public class BetterReadsApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(BetterReadsApplication.class, args);
    }

}
