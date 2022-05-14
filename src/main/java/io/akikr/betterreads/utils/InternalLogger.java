package io.akikr.betterreads.utils;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.TeeOutputStream;
import org.apache.tomcat.util.json.JSONParser;
import org.apache.tomcat.util.json.ParseException;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.mock.web.DelegatingServletInputStream;
import org.springframework.mock.web.DelegatingServletOutputStream;
import org.springframework.util.StringUtils;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * @apiNote To Log internal request-response of a {@link SpringBootApplication}, just create the bean of this class like this:
 * <pre>{@code
 *      @Bean
 *      public InternalLogger getInternalLoggerUtility()
 *      {
 *      	return new InternalLogger(isEnable);
 *      }
 * }</pre>
 */

@Slf4j
public class InternalLogger implements Filter
{
    private final boolean isEnable;

    public InternalLogger(boolean isEnable)
    {
        this.isEnable = isEnable;
    }

    @SneakyThrows
    @Override
    public void doFilter(
            ServletRequest servletRequest,
            ServletResponse servletResponse,
            FilterChain filterChain) throws IOException, ServletException
    {
        if (isEnable)
        {
            CustomHttpRequestWrapper requestWrapper = new CustomHttpRequestWrapper((HttpServletRequest) servletRequest);

            String requestBuilder = "\n====================================== Internal flow starts ======================================"
                    + "\nRequest URI: " + requestWrapper.getRequestURI()
                    + "\nRequest Method: " + requestWrapper.getMethod()
                    + "\nRequest Headers: " + getRequestHeaders(requestWrapper)
                    + "\nRequest Body: " + getRequestBody(requestWrapper)
                    + "\n========================================";
            log.info(requestBuilder);

            CustomHttpResponseWrapper responseWrapper = new CustomHttpResponseWrapper((HttpServletResponse) servletResponse);

            filterChain.doFilter(requestWrapper, responseWrapper);

            String responseBuilder = "\n========================================"
                    + "\nResponse Status: " + responseWrapper.getStatus()
                    + "\nResponse Headers: " + getResponseHeaders(responseWrapper)
                    + "\nResponse Body: " + getResponseBody(responseWrapper)
                    + "\n====================================== Internal flow ends ======================================";
            log.info(responseBuilder);
        }
        else
        {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    private String getRequestHeaders(CustomHttpRequestWrapper requestWrapper)
    {
        List<String> headers = new ArrayList<>();
        Enumeration<String> headerNames = requestWrapper.getHeaderNames();
        while (headerNames.hasMoreElements())
        {
            String headerName = headerNames.nextElement();
            headers.add(headerName + " : " + requestWrapper.getHeader(headerName));
        }
        return headers.toString();
    }

    private String getRequestBody(CustomHttpRequestWrapper requestWrapper)
    {
        return StringUtils.hasText(requestWrapper.getHttpRequestBody()) ? requestWrapper.getHttpRequestBody(): "[no-body]";
    }

    private String getResponseHeaders(CustomHttpResponseWrapper responseWrapper)
    {
        List<String> headers = new ArrayList<>();
        responseWrapper.getHeaderNames().forEach(headerNames -> headers.add(headerNames + " : " + responseWrapper.getHeader(headerNames)));
        return headers.toString();
    }

    private String getResponseBody(CustomHttpResponseWrapper responseWrapper)
    {
        return StringUtils.hasText(responseWrapper.getHttpResponseBody()) ? "[html-body]" : "[no-body]";
    }

    /**
     * CustomHttpRequestWrapper extending HttpServletRequestWrapper
     * is used to modify request parameters in servlet filter.
     * It will help to servlet read request body.
     * Using below given HttpServletRequestWrapper,
     * you can read HTTP request body and then the servlet can still read it later.
     * Essentially, request body content is cached inside wrapper
     * object so it can be N number of times in complete request lifecycle.
     */

    private static class CustomHttpRequestWrapper extends HttpServletRequestWrapper
    {
        private byte[] byteArray;

        public CustomHttpRequestWrapper(HttpServletRequest request)
        {
            super(request);
            try
            {
                this.byteArray = IOUtils.toByteArray(request.getInputStream());
            }
            catch (Exception e)
            {
                log.error(e.getMessage());
            }
        }

        public byte[] getByteArray()
        {
            return byteArray;
        }

        @Override
        public ServletInputStream getInputStream()
        {
            return new DelegatingServletInputStream(new ByteArrayInputStream(getByteArray()));
        }

        public String getHttpRequestBody()
        {
            String httpRequestBody = "";
            try
            {
                String byteArr = new String(getByteArray(), StandardCharsets.UTF_8);
                if (!byteArr.isEmpty())
                {
                    httpRequestBody = new JSONParser(byteArr).parse().toString();
                }
            }
            catch (ParseException e)
            {
                log.error(e.getMessage());
            }
            return httpRequestBody;
        }
    }

    /**
     * CustomHttpRequestWrapper extending HttpServletResponseWrapper
     * is used to modify response parameters in servlet filter.
     * It will help to servlet read response body.
     * Using below given HttpServletResponseWrapper,
     * you can read HTTP response body and then the servlet can still read it later.
     */

    private static class CustomHttpResponseWrapper extends HttpServletResponseWrapper
    {
        private final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        private final PrintStream printStream = new PrintStream(byteArrayOutputStream);

        public CustomHttpResponseWrapper(HttpServletResponse servletResponse)
        {
            super(servletResponse);
        }

        @Override
        public ServletOutputStream getOutputStream() throws IOException
        {
            return new DelegatingServletOutputStream(new TeeOutputStream(super.getOutputStream(), printStream));
        }

        @Override
        public PrintWriter getWriter() throws IOException
        {
            return new PrintWriter(new TeeOutputStream(super.getOutputStream(), printStream));
        }

        public ByteArrayOutputStream getByteArrayOutputStream()
        {
            return byteArrayOutputStream;
        }

        public String getHttpResponseBody()
        {
            return getByteArrayOutputStream().toString();
        }
    }
}
