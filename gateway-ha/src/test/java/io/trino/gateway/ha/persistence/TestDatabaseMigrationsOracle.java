/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.trino.gateway.ha.persistence;

import com.google.common.collect.ImmutableList;
import org.jdbi.v3.core.Handle;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.OracleContainer;

import java.util.List;

import static java.lang.String.format;

final class TestDatabaseMigrationsOracle
        extends BaseTestDatabaseMigrations
{
    @Override
    protected final JdbcDatabaseContainer<?> startContainer()
    {
        JdbcDatabaseContainer<?> container = new OracleContainer("gvenzl/oracle-xe:18.4.0-slim");
        container.start();
        return container;
    }

    @Override
    protected final String getDriver()
    {
        return "oracle.jdbc.driver.OracleDriver";
    }

    @Override
    public void testMigrationWithExistingGatewaySchema()
    {
        /*
         * We do not need to run this test with Oracle since Oracle
         * backend DB support did not exist before so there
         * can be no existing deployments of gateway using
         * Oracle as the backend database.
         */
    }

    @Override
    protected void dropAllTables()
    {
        List<String> tables = ImmutableList.of("gateway_backend", "query_history", "resource_groups_global_properties", "selectors", "resource_groups", "exact_match_source_selectors", "\"flyway_schema_history\"");
        Handle jdbiHandle = jdbi.open();
        String sql = format("SELECT 1 FROM all_tables WHERE owner = '%s'", getTestSchema());
        verifyResultSetCount(sql, 7);
        tables.forEach(table -> jdbiHandle.execute(format("DROP TABLE %s", table)));
        verifyResultSetCount(sql, 0);
        jdbiHandle.close();
    }

    @Override
    protected void createGatewaySchema()
    {
        /*
         * we do not create a schema because we are not running the testMigrationWithExistingGatewaySchema
         * test against Oracle. This is the only test we need to create the gateway schema
         * manually for.
         */
    }

    @Override
    protected String getTestSchema()
    {
        return "TEST";
    }
}