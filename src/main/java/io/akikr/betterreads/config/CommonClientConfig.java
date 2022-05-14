package io.akikr.betterreads.config;

import io.akikr.betterreads.utils.InternalLogger;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @apiNote : The common configuration of the application
 * @author ankit
 * @since 1.0
 */

@Slf4j
@Configuration
public class CommonClientConfig
{
	@Value("${app.web-client.connection-timeout:10000}")
	private int connectionTimeout;

	@Value("${app.web-client.read-timeout:10000}")
	private int socketReadTimeout;

	@Value("${app.internal-logger-enable:false}")
	private boolean isEnable;

	/**
	 * @apiNote To Log internal request-response of this application :
	 * {@link io.akikr.betterreads.BetterReadsApplication}
	 * @return {@link InternalLogger}
	 */
	@Bean
	public InternalLogger getInternalLogger()
	{
		return new InternalLogger(isEnable);
	}

	/**
	 * @apiNote Works for both http & https, but for https SSL configs must be provided and isSslEnabled should be true,
	 * otherwise it may throw an SSL-Handshake exception if the SSL config do not match with the server
	 * @return {@link SslContextFactory.Client}
	 */
	public SslContextFactory.Client getSslContextFactory()
	{
		SslContextFactory.Client sslContextFactory = new SslContextFactory.Client();
		try
		{
			log.info("Setting SSL properties: [ConnectTimeout: " + connectionTimeout + ", ReadTimeout: " + socketReadTimeout + "]");
			sslContextFactory.setSslSessionTimeout(connectionTimeout);
			sslContextFactory.setStopTimeout(socketReadTimeout);
		}
		catch (Exception e)
		{
			log.error("Error occurred while setting the SSL properties for SslContextFactory: " + e.getMessage());
		}
		return sslContextFactory;
	}
}
