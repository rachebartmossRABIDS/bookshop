
package cn.edu.hbpu.configuration;

import org.springframework.context.annotation.*;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

@Configuration
@ComponentScan({"cn.edu.hbpu.service"})
@PropertySource({"classpath:jdbc.properties"})
@Import({JDBCConfig.class, MybatisConfig.class})
@EnableAspectJAutoProxy
public class SpringConfig {

    @Bean
    public MultipartResolver multipartResolver() {
        CommonsMultipartResolver resolver = new CommonsMultipartResolver();
        resolver.setMaxUploadSize(1024 * 1024 * 10);
        resolver.setDefaultEncoding("UTF-8");
        return resolver;
    }
}
