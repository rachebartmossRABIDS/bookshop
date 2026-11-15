
package cn.edu.hbpu.configuration;

import com.github.pagehelper.PageInterceptor;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.AutoMappingBehavior;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
public class MybatisConfig {

    @Bean
    public SqlSessionFactoryBean sqlSessionFactory(
            DataSource dataSource,
            Interceptor[] interceptors) throws Exception {

        SqlSessionFactoryBean ssfb = new SqlSessionFactoryBean();

        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
        // 带嵌套查询时，默认 PARTIAL 不会回填订单号、评论内容等普通字段
        configuration.setAutoMappingBehavior(AutoMappingBehavior.FULL);
        ssfb.setConfiguration(configuration);
        ssfb.setTypeAliasesPackage("cn.edu.hbpu.pojo");
        ssfb.setDataSource(dataSource);
        ssfb.setPlugins(interceptors);
        ssfb.setMapperLocations(new PathMatchingResourcePatternResolver()
                .getResources("classpath:cn/edu/hbpu/mapper/*.xml"));

        return ssfb;
    }

    @Bean
    public MapperScannerConfigurer mapperScannerConfigurer() {
        MapperScannerConfigurer msc = new MapperScannerConfigurer();

        msc.setBasePackage("cn.edu.hbpu.mapper");

        return msc;
    }

    @Bean
    public Interceptor getPageInterceptor() {
        PageInterceptor pageInterceptor = new PageInterceptor();

        Properties props = new Properties();
        props.setProperty("helperDialect", "mysql");
        props.setProperty("reasonable", "true");

        pageInterceptor.setProperties(props);

        return pageInterceptor;
    }
}
