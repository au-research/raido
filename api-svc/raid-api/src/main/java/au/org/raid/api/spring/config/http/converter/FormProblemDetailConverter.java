package au.org.raid.api.spring.config.http.converter;

import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static java.util.Collections.singletonList;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;

/**
 * Written because the default FormHttpMessageConverter can't handle a
 * ProblemDetail, it can only convert MultiValueMap instances.
 * `No converter for [class org.springframework.http.ProblemDetail] with preset Content-Type 'application/x-www-form-urlencoded`
 * This implementation was just adapted from FormHttpMessageConverter but
 * hardcoded for ProblemDetail.
 */
public class FormProblemDetailConverter implements
        HttpMessageConverter<ProblemDetail> {
    List<MediaType> supportedMediaTypes =
            singletonList(APPLICATION_FORM_URLENCODED);

    private static Charset getCharset(MediaType contentType) {
        if (contentType == null) {
            return StandardCharsets.UTF_8;
        }
        if (contentType.getCharset() == null) {
            return StandardCharsets.UTF_8;
        }
        return contentType.getCharset();
    }

    @Override
    public boolean canRead(Class<?> clazz, MediaType mediaType) {
        return false;
    }

    @Override
    public boolean canWrite(Class<?> clazz, MediaType mediaType) {
        if (!APPLICATION_FORM_URLENCODED.isCompatibleWith(mediaType)) {
            return false;
        }

        return clazz.isAssignableFrom(ProblemDetail.class);
    }

    @Override
    public List<MediaType> getSupportedMediaTypes() {
        return supportedMediaTypes;
    }

    @Override
    public ProblemDetail read(
            Class<? extends ProblemDetail> clazz,
            HttpInputMessage inputMessage
    ) throws IOException, HttpMessageNotReadableException {
        throw new UnsupportedOperationException(
                getClass().getName() + " doesn't know how to read");
    }

    @Override
    public void write(
            ProblemDetail problemDetail,
            MediaType contentType,
            HttpOutputMessage outputMessage
    ) throws IOException, HttpMessageNotWritableException {
        Charset charset = getCharset(contentType);
        byte[] bytes = serializeAsForm(problemDetail, charset).getBytes(charset);
        outputMessage.getHeaders().setContentLength(bytes.length);

        if (
                outputMessage instanceof StreamingHttpOutputMessage streamingOutputMessage
        ) {
            streamingOutputMessage.setBody(outputStream ->
                    StreamUtils.copy(bytes, outputStream)
            );
        } else {
            StreamUtils.copy(bytes, outputMessage.getBody());
        }
    }

    protected String serializeAsForm(ProblemDetail problem, Charset charset) {
        StringBuilder builder = new StringBuilder();

        LinkedMultiValueMap<String, Object> writeFields =
                new LinkedMultiValueMap<>();
        if (problem.getProperties() != null) {
            problem.getProperties().forEach((key, value) -> {
                writeFields.put(key, singletonList(value));
            });
        }
        writeFields.put("detail", singletonList(problem.getDetail()));
        writeFields.put("instance", singletonList(problem.getInstance()));
        writeFields.put("status", singletonList(problem.getStatus()));
        writeFields.put("type", singletonList(problem.getType()));
        writeFields.put("title", singletonList(problem.getTitle()));

        writeFields.forEach((name, values) -> {
            if (name == null) {
                // dunno what to do about this
                return;
            }
            values.forEach(value -> {
                if (builder.length() != 0) {
                    builder.append('&');
                }
                builder.append(URLEncoder.encode(name, charset));
                if (value != null) {
                    builder.append('=');
                    builder.append(URLEncoder.encode(String.valueOf(value), charset));
                }
            });
        });

        return builder.toString();
    }

}