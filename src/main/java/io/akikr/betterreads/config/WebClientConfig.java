package io.akikr.betterreads.config;

import io.akikr.betterreads.utils.WebClientLogger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.JettyClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * @apiNote : The WebClient configuration
 * @author ankit
 * @since 1.0
 */

@Configuration
public class WebClientConfig
{
    @Value("${app.web-client.max-memory-buffer-size:262144}")
    private int maxMemoryBufferSize;

    @Value("${app.web-client.logger-enable:false}")
    private boolean isEnable;

    private final CommonClientConfig commonClientConfig;

    public WebClientConfig(CommonClientConfig commonClientConfig)
    {
        this.commonClientConfig = commonClientConfig;
    }

    /**
     * @apiNote The WebClient bean along with WebClientLogger interceptor attached to it for logging
     * @return {@link WebClient}
     */
    @Bean("webClient")
    public WebClient getWebClient()
    {
        return WebClient.builder()
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(clientCodecConfigurer -> clientCodecConfigurer.defaultCodecs()
                                .maxInMemorySize(maxMemoryBufferSize))
                        .build())
                .clientConnector(new JettyClientHttpConnector(new WebClientLogger(isEnable)
                        .getHttpClient(commonClientConfig.getSslContextFactory())))
                .build();
    }
}
