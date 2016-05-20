package com.limpygnome.projectsandbox.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;

import static com.limpygnome.projectsandbox.shared.jpa.JpaConstants.PERSISTENCE_UNIT_NAME;

/**
 * Configuration for JPA / database.
 */
@Configuration
@EnableJpaRepositories
@EnableTransactionManagement
@ComponentScan({
        "com.limpygnome.projectsandbox.*.repository"
})
public class JpaConfig
{

    @Bean
    public EntityManagerFactory entityManagerFactoryMain()
    {
        LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();

        factoryBean.setPersistenceUnitName(PERSISTENCE_UNIT_NAME);
        factoryBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        factoryBean.afterPropertiesSet();

        return factoryBean.getObject();
    }

    @Bean
    public PlatformTransactionManager platformTransactionManagerMain()
    {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactoryMain());
        return transactionManager;
    }

}
