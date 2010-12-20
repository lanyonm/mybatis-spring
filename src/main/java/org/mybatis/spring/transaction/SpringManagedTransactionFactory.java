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

import java.sql.Connection;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.ibatis.transaction.Transaction;
import org.apache.ibatis.transaction.TransactionFactory;
import org.springframework.util.Assert;

/**
 * Creates a {@code SpringManagedTransaction}.
 * 
 * @version $Id$
 */
public class SpringManagedTransactionFactory implements TransactionFactory {

    private final DataSource dataSource;

    public SpringManagedTransactionFactory(DataSource dataSource) {
        Assert.notNull("No DataSource specified");

        this.dataSource = dataSource;
    }

    /**
     * {@inheritDoc}
     */
    public Transaction newTransaction(Connection conn, boolean autoCommit) {
        return new SpringManagedTransaction(conn, this.dataSource);
    }

    /**
     * {@inheritDoc}
     */
    public void setProperties(Properties props) {
        // not needed in this version
    }

}
