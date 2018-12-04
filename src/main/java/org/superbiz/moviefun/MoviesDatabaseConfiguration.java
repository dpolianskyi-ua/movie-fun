package org.superbiz.moviefun;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
public class MoviesDatabaseConfiguration {
    @Bean
    public DataSource moviesDataSource(DatabaseServiceCredentials databaseServiceCredentials) {
        MysqlDataSource moviesDataSource = new MysqlDataSource();
        moviesDataSource.setURL(databaseServiceCredentials.jdbcUrl("movies-mysql"));

        HikariDataSource hikariDataSource = new HikariDataSource();

        hikariDataSource.setDataSource(moviesDataSource);

        return hikariDataSource;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean moviesContainerEntityManagerFactoryBean(DataSource moviesDataSource,
                                                                                          HibernateJpaVendorAdapter adapter) {
        LocalContainerEntityManagerFactoryBean bean = new LocalContainerEntityManagerFactoryBean();

        bean.setDataSource(moviesDataSource);
        bean.setJpaVendorAdapter(adapter);
        bean.setPackagesToScan("org.superbiz.moviefun.movies");
        bean.setPersistenceUnitName("movies");

        return bean;
    }

    @Bean
    public PlatformTransactionManager moviesPlatformTransactionManager(EntityManagerFactory moviesContainerEntityManagerFactoryBean) {
        JpaTransactionManager moviesJpaTransactionManager = new JpaTransactionManager();
        moviesJpaTransactionManager.setEntityManagerFactory(moviesContainerEntityManagerFactoryBean);
        return moviesJpaTransactionManager;
    }

    @Bean
    public TransactionTemplate moviesTransactionTemplate(PlatformTransactionManager moviesPlatformTransactionManager) {
        return new TransactionTemplate(moviesPlatformTransactionManager);
    }
}
