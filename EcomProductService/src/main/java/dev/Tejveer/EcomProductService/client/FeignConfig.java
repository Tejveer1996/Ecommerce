package dev.Tejveer.EcomProductService.client;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor requestInterceptor(){
        return requestTemplate -> {
            ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder
                    .getRequestAttributes();
            String authorization = servletRequestAttributes.getRequest()
                    .getHeader("Authorization");
            if (authorization!=null){
                requestTemplate.header("Authorization",authorization);
            }
        };
    }
}
