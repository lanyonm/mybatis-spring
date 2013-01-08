package org.mybatis.spring.mapper;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import org.mybatis.spring.mapper.annotation.EnableMapperScanning;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.StringUtils;

/**
 * A {@link ImportBeanDefinitionRegistrar} to allow annotation configuration of
 * MyBatis mapper scanning. Using an @Enable annotation allows beans to be
 * registered via @Component configuration, whereas implementing
 * {@code BeanDefinitionRegistryPostProcessor} will work for XML configuration.
 * 
 * @author lanyonm
 * @see MapperFactoryBean
 * @since 1.1.2
 * @version $Id$
 */
public class MapperBeanRegistrar implements ImportBeanDefinitionRegistrar {

  public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
    AnnotationAttributes annoAttrs = AnnotationAttributes.fromMap(importingClassMetadata.getAnnotationAttributes(EnableMapperScanning.class.getName()));

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
    MapperScanner scanner = new MapperScanner(registry, false);
    Class<? extends Annotation> annotationClass = annoAttrs.getClass("annotationClass");
    if (!Annotation.class.equals(annotationClass)) {
      scanner.setAnnotationClass(annotationClass);
    }
    Class<?> markerInterface = annoAttrs.getClass("markerInterface");
    if (!Class.class.equals(markerInterface)) {
      scanner.setMarkerInterface(markerInterface);
    }
    String sqlSessionTemplateBeanName = annoAttrs.getString("sqlSessionTemplateBeanName");
    if (StringUtils.hasText(sqlSessionTemplateBeanName)) {
      scanner.setSqlSessionTemplateBeanName(sqlSessionTemplateBeanName);
    }
    String sqlSessionFactoryBeanName = annoAttrs.getString("sqlSessionFactoryBeanName");
    if (StringUtils.hasText(sqlSessionFactoryBeanName)) {
      scanner.setSqlSessionFactoryBeanName(sqlSessionFactoryBeanName);
    }
    scanner.registerFilters();
    scanner.doScan(StringUtils.toStringArray(basePackages));
  }

}
