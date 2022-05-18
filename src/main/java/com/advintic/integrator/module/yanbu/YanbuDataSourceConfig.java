package com.advintic.integrator.module.yanbu;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;

import org.springframework.boot.context.properties.ConfigurationProperties;

import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "com.advintic.integrator.module.yanbu",
        entityManagerFactoryRef = "yanbuEntityManagerFactory",
        transactionManagerRef= "yanbuTransactionManager"
)
public class YanbuDataSourceConfig {

    @Bean(name="yanbuDataSource")
    //@Primary
    @ConfigurationProperties(prefix="spring.datasource.yanbu")
    public DataSource yanbuDataSource() {
        return DataSourceBuilder.create().build();
    }

    //@Primary
    @Bean(name = "yanbuEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean yanbuEntityManagerFactory(EntityManagerFactoryBuilder builder,
                                                                              @Qualifier("yanbuDataSource") DataSource yanbuDataSource) {
        return builder
                .dataSource(yanbuDataSource)
                .packages("com.advintic.integrator.module.yanbu")
                .build();
    }

    @Bean(name = "yanbuTransactionManager")
    public PlatformTransactionManager yanbuTransactionManager(
            @Qualifier("yanbuEntityManagerFactory") EntityManagerFactory yanbuEntityManagerFactory) {
        return new JpaTransactionManager(yanbuEntityManagerFactory);
    }
}