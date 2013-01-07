package org.mybatis.spring.mapper;

import java.util.ArrayList;
import java.util.List;

import org.mybatis.spring.mapper.annotation.EnableMyBatisMapperScanner;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.StringUtils;

/**
 * A {@link ImportBeanDefinitionRegistrar} to allow annotation configuration
 * of MyBatis mapper scanning.  Using an @Enable annotation allows beans to
 * be registered via @Component configuration, whereas implementing
 * {@code BeanDefinitionRegistryPostProcessor} will work for XML configuration.
 * 
 * @author lanyonm
 * @see MapperFactoryBean
 * @since 1.1.2
 * @version $Id$
 */
public class MapperBeanRegistrar implements ImportBeanDefinitionRegistrar {

	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
		AnnotationAttributes annoAttrs = AnnotationAttributes.fromMap(
				importingClassMetadata.getAnnotationAttributes(EnableMyBatisMapperScanner.class.getName()));
		
		List<String> basePackages = new ArrayList<String>();
		for (String pkg : annoAttrs.getStringArray("value")) {
			if (StringUtils.hasText(pkg)) {
				basePackages.add(pkg);
			}
		}
		for (String pkg : annoAttrs.getStringArray("basePackages")) {
			if (StringUtils.hasText(pkg)) {
				basePackages.add(pkg);
			}
		}

		MapperScanner scanner = new MapperScanner(registry);
		scanner.doScan(StringUtils.toStringArray(basePackages));
	}

}
