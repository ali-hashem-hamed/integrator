package com.advintic.integrator.module.irc;

import com.microsoft.sqlserver.jdbc.SQLServerDriver;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
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

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "com.advintic.integrator.module.irc",
        entityManagerFactoryRef = "ircEntityManagerFactory",
        transactionManagerRef= "ircTransactionManager"
)
public class IRCDataSourceConfig {

    @Value("${irc.lisenter.db.activate}")
    boolean dbTabelListenerActivated;

    @Bean(name="ircDataSource")
    @Primary
    @ConfigurationProperties(prefix="spring.datasource.irc")
    public DataSource ircDataSource() {

        return DataSourceBuilder.create().build();
    }

    @Primary
    @Bean(name = "ircEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean ircEntityManagerFactory(EntityManagerFactoryBuilder builder,
                                                                              @Qualifier("ircDataSource") DataSource ircDataSource) {
        return builder
                .dataSource(ircDataSource)
                .packages("com.advintic.integrator.module.irc")
                .build();
    }

    @Bean(name = "ircTransactionManager")
    public PlatformTransactionManager ircTransactionManager(
            @Qualifier("ircEntityManagerFactory") EntityManagerFactory ircEntityManagerFactory) {
        return new JpaTransactionManager(ircEntityManagerFactory);
    }
}