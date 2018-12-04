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
public class AlbumsDatabaseConfiguration {
    @Bean
    public DataSource albumsDataSource(DatabaseServiceCredentials databaseServiceCredentials) {
        MysqlDataSource albumsDataSource = new MysqlDataSource();
        albumsDataSource.setURL(databaseServiceCredentials.jdbcUrl("albums-mysql"));

        HikariDataSource hikariDataSource = new HikariDataSource();

        hikariDataSource.setDataSource(albumsDataSource);

        return hikariDataSource;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean albumsContainerEntityManagerFactoryBean(DataSource albumsDataSource,
                                                                                          HibernateJpaVendorAdapter adapter) {
        LocalContainerEntityManagerFactoryBean bean = new LocalContainerEntityManagerFactoryBean();

        bean.setDataSource(albumsDataSource);
        bean.setJpaVendorAdapter(adapter);
        bean.setPackagesToScan("org.superbiz.moviefun.albums");
        bean.setPersistenceUnitName("albums");

        return bean;
    }

    @Bean
    public PlatformTransactionManager albumsPlatformTransactionManager(EntityManagerFactory albumsContainerEntityManagerFactoryBean) {
        JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();

        jpaTransactionManager.setEntityManagerFactory(albumsContainerEntityManagerFactoryBean);

        return jpaTransactionManager;
    }

    @Bean
    public TransactionTemplate albumsTransactionTemplate(PlatformTransactionManager albumsPlatformTransactionManager) {
        return new TransactionTemplate(albumsPlatformTransactionManager);
    }
}
