/*
 *    Copyright 2010 The myBatis Team
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.mybatis.spring.annotation;

import java.beans.Introspector;
import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Set;

import org.apache.ibatis.io.ResolverUtil;
import org.apache.ibatis.io.ResolverUtil.Test;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

/**
 * BeanDefinitionRegistryPostProcessor that searches recursively
 * starting from a basePackage for interfaces with {@link Mapper} annotation
 * and registers MapperFactoryBeans.
 * <p>
 * It is usually used with autowire enabled so all the beans it creates are
 * automatically autowired with the proper {@link SqlSessionFactory} or
 * {@link SqlSessionTemplate} 
 * <p>
 * It there is more than one DataSource or {@link SqlSessionFactory} in the application
 * autowire cannot be used. In this case you can specify
 * {@link SqlSessionFactory} or {@link SqlSessionTemplate} to use.
 * <p>
 * When specifying any of these beans notice that <b>bean names</b> must be
 * used instead of real references. It has to be this way because
 * the MapperScannerPostProcessor runs very early in the Spring startup process
 * and some other post processors have not started yet (like PropertyPlaceholderConfigurer)
 * and if they are needed (for example to setup the DataSource) the start process
 * will fail.
 * <p>
 * Configuration sample:
 * <p>
 * <pre class="code">
 * {@code
 *   <bean class="org.mybatis.spring.annotation.MapperScannerPostProcessor">
 *       <property name="basePackage" value="org.mybatis.spring.sample.mapper" />
 *       <!-- optional, notice that "value" is used, not "ref" -->
 *       <property name="sqlSessionFactoryBeanName" value="sqlSessionFactory" />
 *   </bean>
 * }
 * </pre>
 *
 * @see org.apache.ibatis.session.SqlSessionFactory
 * @see org.mybatis.spring.MapperFactoryBean
 * @version $Id$
 */
public class MapperScannerPostProcessor implements BeanDefinitionRegistryPostProcessor, InitializingBean {

    private final Log logger = LogFactory.getLog(this.getClass());

    private String basePackage; // TODO should accept patterns
    
    private Class<?> superType;

    private Class<? extends Annotation> annotation = Mapper.class; // TODO this should not be the default 

    private boolean addToConfig = true;

    private String sqlSessionTemplateBeanName;

    private String sqlSessionFactoryBeanName;
    
    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }

    public void setAddToConfig(boolean addToConfig) {
        this.addToConfig = addToConfig;
    }

    public void setSqlSessionTemplateBeanName(String sqlSessionTemplateName) {
        this.sqlSessionTemplateBeanName = sqlSessionTemplateName;
    }

    public void setSqlSessionFactoryBeanName(String sqlSessionFactoryName) {
        this.sqlSessionFactoryBeanName = sqlSessionFactoryName;
    }

    public void setSuperType(Class<?> superType) {
        this.superType = superType;
    }

    public void setAnnotation(Class<? extends Annotation> annotation) {
        this.annotation = annotation;
    }

    /**
     * {@inheritDoc}
     */
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(this.basePackage, "Property 'basePackage' is required");
    }

    /**
     * {@inheritDoc}
     */
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
        // not needed in this version
    }

    /**
     * {@inheritDoc}
     */
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) {
        Set<Class<?>> mapperInterfaces = searchForMappers();
        if (mapperInterfaces.isEmpty()) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("No MyBatis mapper was found in '"
                        + this.basePackage
                        + "' package. Make sure your mappers are annotated with @Mapper");
            }
        } else {
            registerMappers(registry, mapperInterfaces);
        }
    }

    private Set<Class<?>> searchForMappers() {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Searching for MyBatis mappers in '"
                        + this.basePackage
                        + "' package");
        }

        String[] basePackagesArray = 
            StringUtils.tokenizeToStringArray(this.basePackage, ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS);
        ResolverUtil<Object> resolverUtil = new ResolverUtil<Object>();
        Test test = null;
        if (this.superType != null) {
            test = new ResolverUtil.IsA(superType);
        } else if (this.annotation != null) {
            test = new ResolverUtil.AnnotatedWith(annotation);            
        } else {
            test = new ResolverUtil.Test() {
                public boolean matches(Class<?> type) {
                    return type.isInterface() && type.getMethods().length > 0;
                }
            };
        }
        
        for (String packageName : basePackagesArray) {
            resolverUtil.find(test, packageName);
        }

        // isA() also returns the marker interface. 
        // remove it if it has no methods
        Set<Class<?>> candidates = resolverUtil.getClasses();
        if (superType != null && superType.getMethods().length > 0) {
            candidates.remove(superType);
        }
        
        return candidates;
    }

    private void registerMappers(BeanDefinitionRegistry registry, Set<Class<?>> mapperInterfaces) {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Registering MyBatis mappers");
        }

        // if no annotation was specified lets try with @Named to get the bean name
        if (annotation == null) {
            annotation = getNamedAnnotation();
        }

        for (Class<?> mapperInterface : mapperInterfaces) {
            BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(InternalMapperFactoryBean.class);
            beanDefinitionBuilder.addPropertyValue("mapperInterface", mapperInterface);
            beanDefinitionBuilder.addPropertyValue("addToConfig", this.addToConfig);
            if (StringUtils.hasLength(this.sqlSessionFactoryBeanName)) {
                beanDefinitionBuilder.addPropertyReference("sqlSessionFactory", this.sqlSessionFactoryBeanName);
            }
            if (StringUtils.hasLength(this.sqlSessionTemplateBeanName)) {
                beanDefinitionBuilder.addPropertyReference("sqlSessionTemplate", this.sqlSessionTemplateBeanName);
            }
            
            // Spring style default name 
            String name = buildDefaultBeanName(mapperInterface.getName());
            
            if (annotation != null) {
                Annotation namedAnnotation = mapperInterface.getAnnotation(annotation);
                if (namedAnnotation != null) {
                    Map<String, Object> annotationAtributes = AnnotationUtils.getAnnotationAttributes(namedAnnotation, true);
                    name = (String) annotationAtributes.get("value");
                }
            }            

            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Registering MyBatis mapper with '"
                        + name + "' name and '" 
                        + mapperInterface.getName() + "' mapperInterface");
            }

            registry.registerBeanDefinition(name, beanDefinitionBuilder.getBeanDefinition());
        }
    }

    private String buildDefaultBeanName(String name) {
        String shortClassName = ClassUtils.getShortName(name);
        return Introspector.decapitalize(shortClassName);
    }

    @SuppressWarnings("unchecked")
    private Class<? extends Annotation> getNamedAnnotation() {
        Class<? extends Annotation> namedAnnotation = null;
        try {
            ClassLoader cl = MapperScannerPostProcessor.class.getClassLoader();
            namedAnnotation = (Class<? extends Annotation>) cl.loadClass("javax.inject.Named");
            if (logger.isDebugEnabled()) {
                logger.debug("JSR-330 'javax.inject.Named' annotation found");
            }
        } catch (ClassNotFoundException ex) {
            // JSR-330 API not available
        }
        return namedAnnotation;
    }
}
