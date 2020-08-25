package com.savog.doopooding.core.configuration.database

import com.savog.doopooding.core.exposed.SpringExposedTransactionManager
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.transaction.annotation.EnableTransactionManagement
import org.springframework.transaction.support.TransactionTemplate
import javax.sql.DataSource

@Configuration
@EnableTransactionManagement
class PrimaryDatabaseConfiguration {
    @Bean
    @Primary
    fun springTransactionManager(dataSource: DataSource): SpringExposedTransactionManager {
        return SpringExposedTransactionManager(dataSource, logging = true)
    }

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource")
    fun dataSourceProperties(): DataSourceProperties {
        return DataSourceProperties()
    }

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource.hikari")
    fun dataSource(): DataSource {
        return dataSourceProperties().initializeDataSourceBuilder().type(HikariDataSource::class.java).build()
    }

    @Bean
    @Primary
    fun transactionTemplate(transactionManager: SpringExposedTransactionManager): TransactionTemplate {
        return TransactionTemplate(transactionManager)
    }

    @Bean
    @Primary
    fun database(dataSource: DataSource): Database {
        return Database.connect(dataSource)
    }
}