package org.mybatis.spring.sample;

import org.mybatis.spring.mapper.annotation.EnableMyBatisMapperScanner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.test.context.ContextConfiguration;

/**
 * Test to ensure that the {@link EnableMyBatisMapperScanner#value()}
 * annotation works as expected.
 * 
 * @author lanyonm
 * @since 1.1.2
 * @version $Id$
 */
@ContextConfiguration
public class SampleEnableAnnotationValueTest extends AbstractSampleTest {

	@Configuration
	@ImportResource("classpath:org/mybatis/spring/sample/config/applicationContext-infrastructure.xml")
	@EnableMyBatisMapperScanner("org.mybatis.spring.sample.dao")
	static class AppConfig {
	}
}
