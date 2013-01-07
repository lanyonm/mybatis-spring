package org.mybatis.spring.sample;

import org.mybatis.spring.mapper.annotation.EnableMyBatisMapperScanner;
import org.mybatis.spring.sample.config.AppConfig1;
import org.springframework.test.context.ContextConfiguration;

/**
 * Test to ensure that the {@link EnableMyBatisMapperScanner#value()}
 * annotation works as expected.
 * 
 * @author lanyonm
 * @since 1.1.2
 * @version $Id$
 */
@ContextConfiguration(classes = { AppConfig1.class })
public class SampleEnableAnnotationValueTest extends AbstractSampleTest {

}
