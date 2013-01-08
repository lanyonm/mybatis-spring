# MyBatis-Spring [![Build Status](https://travis-ci.org/LanyonM/mybatis-spring.png?branch=master)](https://travis-ci.org/LanyonM/mybatis-spring)
This project was forked from the [MyBatis-Spring](http://www.mybatis.org/spring/) project on January 3rd, 2013 for the purposes of contributing a Java Config ``@Enable`` annotation.  If you are interested in the MyBatis-Spring project, please use the Maven repos or get the latest [here](http://code.google.com/p/mybatis/source/checkout).

To reiterate, the purpose of this repo is to share a proposed contribution.  If you're interested in MyBatis-Spring integration, go [here](http://www.mybatis.org/spring/).

# Java Config
With the release of Spring 3.1, ``@Enable`` annotations are the official way to configure support when using Java configuration.  Please see [this discussion](https://jira.springsource.org/browse/SPR-9464?focusedCommentId=79600&page=com.atlassian.jira.plugin.system.issuetabpanels:comment-tabpanel#comment-79600) for more detail.

## @EnableMyBatisMapperScanner
This class simply mimics the functionality of the ``MapperScannerConfigurer`` with a subset of the configuration options.

## Example
Here's how the annotation could be used with ``@Configuration``:

    @Configuration
    @EnableMyBatisMapperScanner("org.my.pkg.persistence")
    public class AppConfig {
      
        @Bean
        public DataSource dataSource() {
            return new EmbeddedDatabaseBuilder()
                     .setType(EmbeddedDatabaseType.H2)
                     .build();
        }
        
        @Bean
        public SqlSessionFactory sqlSessionFactory() throws Exception {
            SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
            sessionFactory.setDataSource(dataSource());
            sessionFactory.setTypeAliasesPackage("org.my.pkg.domain");
            return sessionFactory.getObject();
        }
    }

# Feedback
Please feel free to give feedback/comments on this repo.