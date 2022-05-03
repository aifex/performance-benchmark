package com.performance.benchmark;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;

@EnableJpaRepositories
@Configuration
public class ServiceLayerConfiguration {
    public ServiceLayerConfiguration() {
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
        factoryBean.setDataSource(dataSource);
        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        factoryBean.setJpaVendorAdapter(vendorAdapter);
        factoryBean.setJpaProperties(additionalProperties());
        factoryBean.setPackagesToScan("com.performance.benchmark.model");
        HibernateJpaDialect hibernateJpaDialect = new HibernateJpaDialect();
        factoryBean.setJpaDialect(hibernateJpaDialect);
        return factoryBean;
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(emf);
        return transactionManager;
    }


    Properties additionalProperties() {
        return new Properties() {
            {  // Hibernate Specific:
                setProperty("hibernate.hbm2ddl.auto", "update");
                setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQL95Dialect");
                setProperty("hibernate.connection.driver_class", "org.postgresql.Driver");
                setProperty("hibernate.jdbc.lob.non_contextual_creation", "true");
                setProperty("openjpa.jdbc.SynchronizeMappings", "buildSchema(ForeignKeys=true)");
                setProperty("hibernate.format_sql", "true");
                setProperty("hibernate.show_sql", "false");
            }
        };
    }
}
