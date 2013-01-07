package org.mybatis.spring.sample;

import org.mybatis.spring.mapper.annotation.EnableMyBatisMapperScanner;
import org.mybatis.spring.sample.config.AppConfig2;
import org.springframework.test.context.ContextConfiguration;

/**
 * Test to ensure that the {@link EnableMyBatisMapperScanner#basePackages()}
 * annotation works as expected.
 * 
 * @author lanyonm
 * @since 1.1.2
 * @version $Id$
 */
@ContextConfiguration(classes = { AppConfig2.class })
public class SampleEnableAnnotationBasePackageTest extends AbstractSampleTest {

}
