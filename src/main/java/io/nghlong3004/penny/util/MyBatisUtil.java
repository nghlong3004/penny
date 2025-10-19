package io.nghlong3004.penny.util;

import io.nghlong3004.penny.configuration.ApplicationConfiguration;
import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.apache.ibatis.logging.stdout.StdOutImpl;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

public final class MyBatisUtil {
    private static final SqlSessionFactory SQL_SESSION_FACTORY = buildSqlSessionFactory();

    public static SqlSession openSession() {
        return SQL_SESSION_FACTORY.openSession();
    }

    private static SqlSessionFactory buildSqlSessionFactory() {
        return new SqlSessionFactoryBuilder().build(getConfiguration());
    }

    private static Configuration getConfiguration() {
        ApplicationConfiguration application = ObjectContainer.getApplication();
        Environment environment = getEnvironment(application);
        Configuration configuration = new Configuration(environment);
        configuration.setMapUnderscoreToCamelCase(true);
        configuration.setLogImpl(StdOutImpl.class);
        configuration.addMappers(application.getMybatisPackageName());
        return configuration;
    }

    private static Environment getEnvironment(ApplicationConfiguration application) {
        TransactionFactory transactionFactory = new JdbcTransactionFactory();
        PooledDataSource dataSource = getDataSource(application);
        String id = application.getEnvironment();
        return new Environment(id, transactionFactory, dataSource);
    }

    private static PooledDataSource getDataSource(ApplicationConfiguration application) {
        PooledDataSource dataSource = new PooledDataSource();
        dataSource.setUrl(application.getDataSourceUrl());
        dataSource.setDriver(application.getDataSourceDriverClassName());
        dataSource.setUsername(application.getDataSourceUsername());
        dataSource.setPassword(application.getDataSourcePassword());
        return dataSource;
    }

    private MyBatisUtil() {
    }
}
