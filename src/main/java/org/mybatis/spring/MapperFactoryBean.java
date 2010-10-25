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
package org.mybatis.spring;

import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

/**
 * BeanFactory that enables injection of MyBatis mapper interfaces.
 * It can be setted up with a SqlSessionFactory or a preconfigured SqlSessionTemplate.
 * <p> 
 * Sample configuration:
 *
 * <pre class="code">
 * {@code
 *   <bean id="baseMapper" class="org.mybatis.spring.MapperFactoryBean" abstract="true" lazy-init="true">
 *     <property name="sqlSessionFactory" ref="sqlSesionFactory" />
 *   </bean>
 * 
 *   <bean id="oneMapper" parent="baseMapper">
 *     <property name="mapperInterface" value="my.package.MyMapperInterface" />
 *   </bean>
 *   
 *   <bean id="anotherMapper" parent="baseMapper">
 *     <property name="mapperInterface" value="my.package.MyAnotherMapperInterface" />
 *   </bean>
 * }
 * </pre>
 * @see SqlSessionTemplate
 * @version $Id$
 */
public class MapperFactoryBean<T> implements FactoryBean<T>, InitializingBean {

    private Class<T> mapperInterface;

    private boolean addToConfig = true;

    private SqlSessionTemplate sqlSessionTemplate;

    private boolean externalTemplate;

    @Autowired(required = false)
    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        if (!this.externalTemplate) {
            this.sqlSessionTemplate = new SqlSessionTemplate(sqlSessionFactory);
        }
    }

    @Autowired(required = false)
    public void setSqlSessionTemplate(SqlSessionTemplate sqlSessionTemplate) {
        this.sqlSessionTemplate = sqlSessionTemplate;
        this.externalTemplate = true;
    }

    public void setMapperInterface(Class<T> mapperInterface) {
        this.mapperInterface = mapperInterface;
    }

    public void setAddToConfig(boolean addToConfig) {
        this.addToConfig = addToConfig;
    }

    /**
     * {@inheritDoc}
     */
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(this.mapperInterface, "Property 'mapperInterface' is required");
        Assert.notNull(this.sqlSessionTemplate, "Property 'sqlSessionTemplate' is required");

        this.sqlSessionTemplate.afterPropertiesSet();

        Configuration configuration = this.sqlSessionTemplate.getSqlSessionFactory().getConfiguration();
        if (this.addToConfig && !configuration.hasMapper(mapperInterface)) {
            configuration.addMapper(mapperInterface);
        }
    }

    /**
     * {@inheritDoc}
     */
    public T getObject() throws Exception {
        return this.sqlSessionTemplate.getMapper(mapperInterface);
    }

    /**
     * {@inheritDoc}
     */
    public Class<T> getObjectType() {
        return this.mapperInterface;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isSingleton() {
        return true;
    }

}
