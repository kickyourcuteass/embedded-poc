package ro.home.config;

import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.transaction.PlatformTransactionManager;
import snaq.db.DBPoolDataSource;

import javax.sql.DataSource;

@Configuration
@Import(RootConfig.class)
class JdbcConfig {

    @Bean(destroyMethod = "releaseImmediately")
    DBPoolDataSource springBatchDataSource(Environment env) {
        DBPoolDataSource dataSource = new DBPoolDataSource();
        dataSource.setName("springBatchDataSource");
        dataSource.setDescription("Spring Batch Data Source");
        dataSource.setDriverClassName(env.getProperty("spring.batch.ds.db.driver"));
        dataSource.setUrl(env.getProperty("spring.batch.ds.db.url"));
        dataSource.setUser(env.getProperty("spring.batch.ds.db.user"));
        dataSource.setPassword(env.getProperty("spring.batch.ds.db.pass"));
        dataSource.setMinPool(Integer.valueOf(env.getProperty("spring.batch.ds.min.pool")));
        dataSource.setMaxPool(Integer.valueOf(env.getProperty("spring.batch.ds.max.pool")));
        dataSource.setMaxSize(Integer.valueOf(env.getProperty("spring.batch.ds.max.size")));
        dataSource.setIdleTimeout(3600);
        return dataSource;
    }

    @Bean
    JdbcTemplate springBatchJdbcTemplate(@Qualifier("springBatchDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    DataSourceInitializer springBatchDatabaseInitializer(@Qualifier("springBatchDataSource") DataSource dataSource) {
        DataSourceInitializer dataSourceInitializer = new DataSourceInitializer();
        dataSourceInitializer.setDataSource(dataSource);
        dataSourceInitializer.setDatabasePopulator(new ResourceDatabasePopulator(
        //        new ClassPathResource("org/springframework/batch/core/schema-drop-h2.sql"),
        //        new ClassPathResource("org/springframework/batch/core/schema-h2.sql")
        ));
        return dataSourceInitializer;
    }

    @Bean
    PlatformTransactionManager transactionManager() {
        return new ResourcelessTransactionManager();
    }

    @Bean
    DBPoolDataSource priceStoreDataSource(Environment env) {
        DBPoolDataSource dataSource = new DBPoolDataSource();
        dataSource.setName("priceStoreDataSource");
        dataSource.setDescription("Price Store Data Source");
        dataSource.setDriverClassName(env.getProperty("price.store.ds.db.driver"));
        dataSource.setUrl(env.getProperty("price.store.ds.db.url"));
        dataSource.setUser(env.getProperty("spring.batch.ds.db.user"));
        dataSource.setPassword(env.getProperty("spring.batch.ds.db.pass"));
        dataSource.setMinPool(Integer.valueOf(env.getProperty("price.store.ds.min.pool")));
        dataSource.setMaxPool(Integer.valueOf(env.getProperty("price.store.ds.max.pool")));
        dataSource.setMaxSize(Integer.valueOf(env.getProperty("price.store.ds.max.size")));
        return dataSource;
    }

    @Bean
    DataSourceInitializer priceStoreDatabaseInitializer(@Qualifier("priceStoreDataSource") DataSource dataSource) {
        DataSourceInitializer dataSourceInitializer = new DataSourceInitializer();
        dataSourceInitializer.setDataSource(dataSource);
        dataSourceInitializer.setDatabasePopulator(new ResourceDatabasePopulator(
                new ClassPathResource("jdbc/schemas/stock-prices-db-schema-drop-h2.sql"),
                new ClassPathResource("jdbc/schemas/stock-prices-db-schema-create-h2.sql")
        ));
        return dataSourceInitializer;
    }

    @Bean
    NamedParameterJdbcTemplate priceStoreJdbcTemplate(@Qualifier("priceStoreDataSource") DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }

}
