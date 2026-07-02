package com.icbc.sh.techmg.framework.web;

import com.google.gson.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Configuration
public class GsonConfig implements WebMvcConfigurer {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /** 全局 Gson Bean — 注册 LocalDate/LocalDateTime 适配器，Java 17+ 兼容 */
    @Bean
    public Gson gson() {
        return new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>)
                        (src, type, context) -> new JsonPrimitive(src.format(DATETIME_FMT)))
                .registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>)
                        (json, type, context) -> LocalDateTime.parse(json.getAsString(), DATETIME_FMT))
                .registerTypeAdapter(LocalDate.class, (JsonSerializer<LocalDate>)
                        (src, type, context) -> new JsonPrimitive(src.format(DATE_FMT)))
                .registerTypeAdapter(LocalDate.class, (JsonDeserializer<LocalDate>)
                        (json, type, context) -> LocalDate.parse(json.getAsString(), DATE_FMT))
                .create();
    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.removeIf(c -> c instanceof GsonHttpMessageConverter);

        GsonHttpMessageConverter converter = new GsonHttpMessageConverter();
        converter.setGson(gson());
        converters.add(0, converter);
    }
}
