package org.mybatis.spring.mapper;

import java.lang.annotation.Annotation;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.beans.factory.xml.XmlReaderContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

/**
 * 
 * @since 1.2.0
 *
 */

public class MapperScannerBeanDefinitionParser implements BeanDefinitionParser {

  public synchronized BeanDefinition parse(Element element, ParserContext parserContext) {
    MapperScanner scanner = new MapperScanner(parserContext.getRegistry(), false);
    ClassLoader classLoader = scanner.getResourceLoader().getClassLoader();
    XmlReaderContext readerContext = parserContext.getReaderContext();
    try {
      String annotationClassName = element.getAttribute("annotationClass");
      if (StringUtils.hasText(annotationClassName)) {
        @SuppressWarnings("unchecked")
        Class<? extends Annotation> markerInterface = (Class<? extends Annotation>) classLoader.loadClass(annotationClassName);
        scanner.setAnnotationClass(markerInterface);
      }
      String markerInterfaceClassName = element.getAttribute("markerInterface");
      if (StringUtils.hasText(markerInterfaceClassName)) {
        Class<?> markerInterface = classLoader.loadClass(markerInterfaceClassName);
        scanner.setMarkerInterface(markerInterface);
      }
    } catch (Exception ex) {
      readerContext.error(ex.getMessage(), readerContext.extractSource(element), ex.getCause());
    }
    String sqlSessionTemplateBeanName = element.getAttribute("sqlSessionTemplateBeanName");
    if (StringUtils.hasText(sqlSessionTemplateBeanName)) {
      scanner.setSqlSessionTemplateBeanName(sqlSessionTemplateBeanName);
    }
    String sqlSessionFactoryBeanName = element.getAttribute("sqlSessionFactoryBeanName");
    if (StringUtils.hasText(sqlSessionFactoryBeanName)) {
      scanner.setSqlSessionFactoryBeanName(sqlSessionFactoryBeanName);
    }
    scanner.setResourceLoader(readerContext.getResourceLoader());
    scanner.registerFilters();
    String basePackage = element.getAttribute("basePackage");
    scanner.scan(StringUtils.tokenizeToStringArray(basePackage, ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS));
    return null;
  }

}
