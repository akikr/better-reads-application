package io.akikr.betterreads.db.connection;

import lombok.Data;
import org.springframework.boot.autoconfigure.cassandra.CqlSessionBuilderCustomizer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.io.File;
import java.nio.file.Path;

/**
 * @apiNote : The DataStaxAstra properties loader to connect to Cassandra database.
 * @author ankit
 */

@Data
@ConfigurationProperties(prefix = "datastax.astra")
public class DataStaxAstraProperties
{
    /**
     * @apiNote : secure-connect.zip bundle containing credentials and files that is needed to connect to data-stax-astra
     */
    private File secureConnectBundle;

    /**
     * @apiNote Create a bean of type {@link CqlSessionBuilderCustomizer} to run CQL query on cassandra database
     * hosted on DataStaxAstra using the secure-connect file provided by DataStaxAstra.
     * @param dataStaxAstraProperties {@link DataStaxAstraProperties}
     * @return The {@link CqlSessionBuilderCustomizer}
     */
    @Bean
    public CqlSessionBuilderCustomizer sessionBuilderCustomizer(DataStaxAstraProperties dataStaxAstraProperties)
    {
        Path bundlePath = dataStaxAstraProperties.getSecureConnectBundle().toPath();
        return builder -> builder.withCloudSecureConnectBundle(bundlePath);
    }
}
