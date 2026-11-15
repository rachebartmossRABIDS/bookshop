package cn.edu.hbpu.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@ComponentScan({"cn.edu.hbpu.controller", "cn.edu.hbpu.advice"})
@EnableWebMvc
public class SpringMvcConfig {
}
