package au.org.raid.api.spring.bean;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.singletonList;

@Component
public class Shared {
    /**
     * Without this, @Value annotation don't resolve ${} placeholders
     */
    @Bean
    public static PropertySourcesPlaceholderConfigurer
    propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public static RestTemplate restTemplate(ClientHttpRequestFactory factory) {
        MappingJackson2XmlHttpMessageConverter xmlConverter =
                new MappingJackson2XmlHttpMessageConverter();
        xmlConverter.setSupportedMediaTypes(
                singletonList(MediaType.APPLICATION_XML));

        MappingJackson2HttpMessageConverter jsonConverter =
                new MappingJackson2HttpMessageConverter();

        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
        messageConverters.add(xmlConverter);
        messageConverters.add(jsonConverter);
        messageConverters.add(new FormHttpMessageConverter());

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setMessageConverters(messageConverters);
        restTemplate.setRequestFactory(factory);

        return restTemplate;
    }

    @Bean
    public static ClientHttpRequestFactory clientHttpRequestFactory() {
        return clientHttpRequestFactory(true);
    }

    public static ClientHttpRequestFactory clientHttpRequestFactory(
            boolean followRedirects
    ) {
        OkHttpClient client = new OkHttpClient.Builder().
                followRedirects(followRedirects).
                build();

        return new OkHttp3ClientHttpRequestFactory(client) {
            @Override
            public void destroy() throws IOException {
        /* copy pasted from the spring impl, because it won't do this for a
        provide client. */
                Cache cache = client.cache();
                if (cache != null) {
                    cache.close();
                }
                client.dispatcher().executorService().shutdown();
                client.connectionPool().evictAll();
            }
        };
    }

    /*
     Given that we added WRITE_DATES_AS_TIMESTAMPS for this (which I think was
     for the REST API endpoints - I don't understand why we haven't needed to
     register JavaTimeModule for this? Like the raidMetadata mapper and the
     mapper used by the feign client for integration tests - we had to register
     the module for those usages, why not here?
    */
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper().
                /* from memory, this was to get the Spring REST API endpoints writing
                datetime the way I wanted. */
                        disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS).
                registerModule(new JavaTimeModule());
    }

}
