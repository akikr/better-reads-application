package io.akikr.betterreads.utils;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.springframework.util.StringUtils;

import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;

/**
 * @apiNote This is a {@link org.springframework.web.reactive.function.client.WebClient} logger class that intercept
 * http/https request using {@link org.springframework.http.client.reactive.JettyClientHttpConnector}.
 * It can be used to intercept both asynchronous and synchronous requests.
 * It connects to WebClient using Webclient.clientConnector() as:
 * <pre>{@code
 * Https client with SSL configuration
 *        WebClient.builder()
 *        .clientConnector(new JettyClientHttpConnector(new WebClientLogger(isEnable).getHttpsClient(sslContextFactory)))
 *        .build();
 * }</pre>
 * @author ankit
 * @since 1.0
 */

@Slf4j
public class WebClientLogger
{
	private final boolean isEnable;

	public WebClientLogger(boolean isEnable)
	{
		this.isEnable = isEnable;
	}

	/**
	 * @apiNote The HttpClient with SSL configuration
	 * @return {@link HttpClient}
	 */
	public HttpClient getHttpClient(SslContextFactory.Client sslContextFactory)
	{
		return new HttpClient(sslContextFactory)
		{
			@Override
			public Request newRequest(URI uri)
			{
				Request request = super.newRequest(uri);
				return (isEnable) ? httpClientInterceptor(request) : request;
			}
		};
	}

	/**
	 * @apiNote This takes a Request allowing us to intercept and log the request and response data
	 * and then and gives the request back to flow.
	 */
	private Request httpClientInterceptor(Request inboundRequest)
	{
		StringBuilder requestBuilder = new StringBuilder();
		inboundRequest.onRequestBegin(request -> requestBuilder
				.append("\n---------------------- OUTBOUND REST REQUEST -------------------------")
				.append("\nRequest URI: ").append(request.getURI())
				.append("\nRequest Method: ").append(request.getMethod()));

		inboundRequest.onRequestHeaders(request -> {
			requestBuilder.append("\nRequest Headers: [");
			for (HttpField header : request.getHeaders())
				requestBuilder.append(header.getName()).append(" : ").append(header.getValue()).append(", ");
			requestBuilder.setLength(requestBuilder.length() - 2); //Removing the trailing comma
			requestBuilder.append("]").append("\nRequest Body: ");
		});

		// Get request body
		StringBuilder requestBody = new StringBuilder();
		inboundRequest.onRequestContent((request, content) -> requestBody.append(getBody(content)));

		StringBuilder responseBuilder = new StringBuilder();
		inboundRequest.onResponseBegin(response -> responseBuilder
				.append("\n---------------------- INBOUND REST RESPONSE -------------------------")
				.append("\nResponse Status: ").append(response.getStatus()));

		inboundRequest.onResponseHeaders(response -> {
			responseBuilder.append("\nResponse Headers: [");
			for (HttpField header : response.getHeaders())
				responseBuilder.append(header.getName()).append(" : ").append(header.getValue());
			responseBuilder.setLength(responseBuilder.length() - 2); //Removing the trailing comma
			responseBuilder.append("]").append("\nResponse Body: ");
		});

		// Get response body
		StringBuilder responseBody = new StringBuilder();
		inboundRequest.onResponseContent((response, content) -> responseBody.append(getBody(content)));

		// Actual logging the request & response data
		inboundRequest.onRequestSuccess(request -> {
			String body = requestBody.toString();
			requestBuilder.append(StringUtils.hasText(body) ? body : "[no-body]")
					.append("\n---------------------------------------------------------------------");
			log.info(requestBuilder.toString());
		});
		inboundRequest.onResponseSuccess(response -> {
			String body = responseBody.toString();
			responseBuilder.append(StringUtils.hasText(body) ? body : "[no-body]")
					.append("\n---------------------------------------------------------------------");
			log.info(responseBuilder.toString());
		});

		return inboundRequest;
	}

	private String getBody(ByteBuffer content)
	{
		// Always decode the content into CharBuffer, otherwise reading response-body will throw UnsupportedOperationException
		CharBuffer charBuffer = StandardCharsets.UTF_8.decode(content);
		return charBuffer.toString();
	}
}
