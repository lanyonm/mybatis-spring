package org.mybatis.spring.sample;

import org.mybatis.spring.annotation.EnableMapperScanning;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.test.context.ContextConfiguration;

/**
 * Test to ensure that the {@link EnableMapperScanning#basePackages()}
 * annotation works as expected.
 *
 * @since 1.2.0
 * @version $Id$
 */
@ContextConfiguration
public class SampleEnableTest extends AbstractSampleTest {

	@Configuration
	@ImportResource("classpath:org/mybatis/spring/sample/config/applicationContext-infrastructure.xml")
	@EnableMapperScanning(basePackages = {"org.mybatis.spring.sample.dao"})
	static class AppConfig {
	}
}
