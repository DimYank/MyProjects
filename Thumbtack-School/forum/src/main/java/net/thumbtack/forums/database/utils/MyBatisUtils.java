package net.thumbtack.forums.database.utils;


import net.thumbtack.forums.database.mappers.*;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

public class MyBatisUtils {
    public DataSource dataSource() {
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        return dataSourceBuilder.build();
    }

    private SqlSessionFactory getFactory() throws Exception {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(dataSource());
        SqlSessionFactory sessionFactory = factoryBean.getObject();
        sessionFactory.getConfiguration().addMapper(UserMapper.class);
        return sessionFactory;
    }

    @Bean
    public ForumMapper forumMapper() throws Exception {
        SqlSessionTemplate sqlSessionTemplate = new SqlSessionTemplate(getFactory());
        return sqlSessionTemplate.getMapper(ForumMapper.class);
    }

    @Bean
    public MessageMapper messageMapper() throws Exception {
        SqlSessionTemplate sqlSessionTemplate = new SqlSessionTemplate(getFactory());
        return sqlSessionTemplate.getMapper(MessageMapper.class);
    }

    @Bean
    public UserMapper userMapper() throws Exception {
        SqlSessionTemplate sqlSessionTemplate = new SqlSessionTemplate(getFactory());
        return sqlSessionTemplate.getMapper(UserMapper.class);
    }

    @Bean
    public SessionMapper sessionMapper() throws Exception {
        SqlSessionTemplate sqlSessionTemplate = new SqlSessionTemplate(getFactory());
        return sqlSessionTemplate.getMapper(SessionMapper.class);
    }

    @Bean
    public MessageInfoMapper messageInfoMapper() throws Exception {
        SqlSessionTemplate sqlSessionTemplate = new SqlSessionTemplate(getFactory());
        return sqlSessionTemplate.getMapper(MessageInfoMapper.class);
    }

    @Bean
    public StatisticsMapper statisticsMapper() throws Exception {
        SqlSessionTemplate sqlSessionTemplate = new SqlSessionTemplate(getFactory());
        return sqlSessionTemplate.getMapper(StatisticsMapper.class);
    }

    @Bean
    public DataSourceTransactionManager transactionManager() {
        return new DataSourceTransactionManager(dataSource());
    }

}
