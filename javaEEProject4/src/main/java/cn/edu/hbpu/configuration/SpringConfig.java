
package cn.edu.hbpu.configuration;

import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

@Configuration
@ComponentScan({"cn.edu.hbpu.service"})
@PropertySource({
        "classpath:jdbc.properties",
        "classpath:jwt.properties",
        "classpath:pic.properties",
        "classpath:mail.properties",
        "classpath:redis.properties",
        "classpath:cors.properties"
})
@Import({JDBCConfig.class, MybatisConfig.class, RedisConfig.class})
@EnableAspectJAutoProxy
@EnableTransactionManagement
public class SpringConfig {

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
        return configurer;
    }

    @Bean
    public MultipartResolver multipartResolver() {
        CommonsMultipartResolver resolver = new CommonsMultipartResolver();
        resolver.setMaxUploadSize(1024 * 1024 * 10);
        resolver.setDefaultEncoding("UTF-8");
        return resolver;
    }
}
