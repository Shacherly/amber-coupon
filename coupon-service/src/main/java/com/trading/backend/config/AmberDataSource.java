// package com.trading.backend.coupon.config;
//
//
// import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
// import org.apache.ibatis.session.SqlSessionFactory;
// import org.mybatis.spring.SqlSessionFactoryBean;
// import org.mybatis.spring.SqlSessionTemplate;
// import org.springframework.beans.factory.annotation.Qualifier;
// import org.springframework.boot.context.properties.ConfigurationProperties;
// import org.springframework.boot.jdbc.DataSourceBuilder;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
// import org.springframework.jdbc.datasource.DataSourceTransactionManager;
// import tk.mybatis.spring.annotation.MapperScan;
//
// import javax.sql.DataSource;
//
// @Configuration("tradingDataSourceConfig")
// @MapperScan(
//         basePackages = "com.trading.backend.coupon.tradingmapper",
//         sqlSessionFactoryRef = "tradingSqlSessionFactory"
// )
// public class tradingDataSource {
//
//
//     @Bean("tradingDataSource")
//     @ConfigurationProperties(prefix = "spring.datasource.trading-source")
//     public DataSource tradingDataSource() {
//         return DruidDataSourceBuilder.create().build();
//     }
//
//     @Bean("tradingSqlSessionFactory")
//     public SqlSessionFactory tradingSqlSessionFactory(@Qualifier("tradingDataSource") DataSource dataSource) throws Exception {
//         SqlSessionFactoryBean sqlSessionFactory = new SqlSessionFactoryBean();
//         sqlSessionFactory.setDataSource(dataSource);
//         sqlSessionFactory.setMapperLocations(
//                 new PathMatchingResourcePatternResolver().getResources("classpath*:tradingmapper/*.xml"));
//         sqlSessionFactory.setTypeHandlersPackage("com.trading.backend.coupon.common.type");
//         return sqlSessionFactory.getObject();
//     }
//
//     @Bean(name = "tradingTransactionManager")
//     public DataSourceTransactionManager tradingTransactionManager(@Qualifier("tradingDataSource") DataSource dataSource) {
//         return new DataSourceTransactionManager(dataSource);
//     }
//
//     @Bean(name = "tradingSqlSessionTemplate")
//     public SqlSessionTemplate tradingSqlSessionTemplate(@Qualifier("tradingSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
//         return new SqlSessionTemplate(sqlSessionFactory);
//     }
// }
