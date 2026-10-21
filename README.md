> [!WARNING]
> **This repository is no longer maintained** and has been archived. The patch it proposed was accepted into [MyBatis-Spring](https://github.com/mybatis/spring) in version [1.2.0](https://github.com/mybatis/spring/releases/tag/mybatis-spring-1.2.0). This copy dates from 2013, targets Spring 3.1, and its dependencies are out of date and have known vulnerabilities. Use the upstream project instead.

# MyBatis-Spring
This project was forked from the [MyBatis-Spring](https://mybatis.org/spring/) project on January 3rd, 2013 for the purposes of contributing a Java Config ``@Enable`` annotation. If you are interested in the MyBatis-Spring project, please use the Maven repos or get the latest [here](https://github.com/mybatis/spring).

To reiterate, the purpose of this repo is to share a proposed contribution.  If you're interested in MyBatis-Spring integration, go [here](https://mybatis.org/spring/).

# Java Config
With the release of Spring 3.1, ``@Enable`` annotations are the official way to configure support when using Java configuration.  Please see [this discussion](https://github.com/spring-projects/spring-framework/issues/14099) for more detail.

## @EnableMapperScanning
This class simply uses ``MapperScannerRegistrar`` to mimic the functionality of the ``MapperScannerConfigurer``.

## Example
Here's how the annotation could be used with ``@Configuration``:

    @Configuration
    @EnableMapperScanning("org.my.pkg.persistence")
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
This repository is archived and no longer accepts feedback. Please direct issues to [MyBatis-Spring](https://github.com/mybatis/spring/issues).
