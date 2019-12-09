package ro.home.app.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@ComponentScan(basePackages = {"ro.home.app"})
@EnableWebMvc
public class AppConfig {

    @Bean
    ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
        mapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
        mapper.configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, false);
        return mapper;
    }

    /**
     @Bean(destroyMethod = "releaseImmediately")
     DataSource dataSource() {
     DBPoolDataSource dataSource = new DBPoolDataSource();
     dataSource.setName("dataSource");
     dataSource.setDescription("Data Source");
     dataSource.setDriverClassName("oracle.jdbc.OracleDriver");
     dataSource.setUrl("");
     dataSource.setUser("");
     dataSource.setPassword("");
     dataSource.setMinPool(2);
     dataSource.setMaxPool(15);
     dataSource.setMaxSize(15);
     dataSource.setIdleTimeout(3600);
     return dataSource;
     }
     */
}
