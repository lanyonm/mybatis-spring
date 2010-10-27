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

import java.util.Set;

import org.apache.ibatis.io.ResolverUtil;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * BeanDefinitionRegistryPostProcessor that searches recursively 
 * starting from a basePackage for interfaces with
 * org.mybatis.spring.annotation.Mapper annotation.
 *
 * @see org.apache.ibatis.session.SqlSessionFactory
 * @see org.mybatis.spring.MapperFactoryBean
 * @version $Id$
 */
public class MapperScannerPostProcessor implements BeanDefinitionRegistryPostProcessor, InitializingBean {

    private final Log logger = LogFactory.getLog(this.getClass());

    private String basePackage;

    private boolean addToConfig = true;

    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }

    public void setAddToConfig(boolean addToConfig) {
        this.addToConfig = addToConfig;
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
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        // not needed in this version
    }

    /**
     * {@inheritDoc}
     */
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        Set<Class<?>> mapperInterfaces = searchForMappers();
        if (mapperInterfaces.isEmpty()) {
            if (logger.isDebugEnabled()) {
                logger.debug("No MyBatis mapper was found in '"
                        + this.basePackage
                        + "' package. Make sure your mappers are annotated with @Mapper");
            }
        } else {
            registerMappers(registry, mapperInterfaces);
        }
    }

    private Set<Class<?>> searchForMappers() {
        if (logger.isDebugEnabled()) {
            logger.debug("Searching for MyBatis mappers in '"
                        + this.basePackage
                        + "' package");
        }

        String[] basePackagesArray = 
            StringUtils.tokenizeToStringArray(this.basePackage, ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS);
        ResolverUtil<Object> resolver = new ResolverUtil<Object>();
        resolver.findAnnotated(Mapper.class, basePackagesArray);
        return resolver.getClasses();
    }

    private void registerMappers(BeanDefinitionRegistry registry, Set<Class<?>> mapperInterfaces) {
        if (logger.isDebugEnabled()) {
            logger.debug("Registering MyBatis mappers");
        }

        for (Class<?> mapperInterface : mapperInterfaces) {
            BeanDefinition beanDefinition =
                BeanDefinitionBuilder.genericBeanDefinition(InternalMapperFactoryBean.class).getBeanDefinition();
            MutablePropertyValues mutablePropertyValues = beanDefinition.getPropertyValues();
            mutablePropertyValues.addPropertyValue("mapperInterface", mapperInterface);
            mutablePropertyValues.addPropertyValue("addToConfig", this.addToConfig);
            String name = mapperInterface.getAnnotation(Mapper.class).value();
            if (!StringUtils.hasLength(name)) {
                name = mapperInterface.getName();
            }
            
            if (logger.isDebugEnabled()) {
                logger.debug("Registering MyBatis mapper with '" 
                        + name + "' name and '" 
                        + mapperInterface.getName() + "' mapperInterface");
            }
            
            registry.registerBeanDefinition(name, beanDefinition);
        }
    }

}
