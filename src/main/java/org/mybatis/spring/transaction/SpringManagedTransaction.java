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
package org.mybatis.spring.transaction;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.logging.jdbc.ConnectionLogger;
import org.apache.ibatis.transaction.Transaction;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.util.Assert;

/**
 * MyBatis has two TransactionManagers out of the box: The {@code JdbcTransaction} and the
 * {@code ManagedTransaction}. When MyBatis runs under a Spring transaction none of them
 * will work fine because {@code JdbcTransaction} will commit/rollback/close and it should not
 * and {@code ManagedTransaction} will close the connection and it should not.
 * {@code SpringManagedTransaction} looks if the current connection is being managed by Spring. 
 * In that case it will no-op all commit/rollback/close calls assuming that the Spring 
 * transaction manager will do the job. Otherwise it will behave almost like {@code JdbcTransaction}.
 *
 * @version $Id$
 */
public class SpringManagedTransaction implements Transaction {

    private final Log logger = LogFactory.getLog(getClass());

    private final Connection connection;
    
    private final Connection unwrappedConnection;
    
    private final DataSource dataSource;

    private final boolean isConnectionTransactional;

    /**
     * Constructor that discovers if this {@code Transaction} should manage connection or let it to Spring. It gets both
     * the {@code Connection} and the {@code DataSource} it was built from and asks Spring if they are bundled to the
     * current transaction.
     * 
     * @param connection JDBC connection to manage
     * @param dataSource The {@code DataSource} that was configured in current {@code SqlSessionFactory}
     */
    public SpringManagedTransaction(Connection connection, DataSource dataSource) {
        Assert.notNull(connection, "No Connection specified");
        Assert.notNull(dataSource, "No DataSource specified");

        this.connection = connection;
        this.dataSource = dataSource;
        this.unwrappedConnection = unwrapConnection(connection);
        this.isConnectionTransactional = DataSourceUtils.isConnectionTransactional(this.unwrappedConnection, dataSource);

        if (this.logger.isDebugEnabled()) {
            this.logger.debug(
                    "JDBC Connection [" 
                    + this.connection 
                    + "] will" 
                    + (this.isConnectionTransactional?" ":" not ") 
                    + "be managed by Spring");
        }
    }

    /**
     * {@inheritDoc}
     */
    public Connection getConnection() {
        return this.connection;
    }

    /**
     * {@inheritDoc}
     */
    public void commit() throws SQLException {
        if (!this.isConnectionTransactional) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Committing JDBC Connection [" + this.connection + "]");
            }
            this.connection.commit();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void rollback() throws SQLException {
        if (!this.isConnectionTransactional) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Rolling back JDBC Connection [" + this.connection + "]");
            }
            this.connection.rollback();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void close() throws SQLException {
        DataSourceUtils.releaseConnection(this.unwrappedConnection, this.dataSource);
    }

    /**
     * MyBatis wraps the JDBC Connection with a logging proxy but Spring registers the original connection so it should
     * be unwrapped before calling {@code DataSourceUtils.isConnectionTransactional(Connection, DataSource)}
     * 
     * @param connection May be a {@code ConnectionLogger} proxy
     * @return the original JDBC {@code Connection}
     */
    private Connection unwrapConnection(Connection connection) {
        if (Proxy.isProxyClass(connection.getClass())) {
            InvocationHandler handler = Proxy.getInvocationHandler(connection);
            if (handler instanceof ConnectionLogger) {
                return ((ConnectionLogger) handler).getConnection();
            }
        }
        return connection;
    }

}
