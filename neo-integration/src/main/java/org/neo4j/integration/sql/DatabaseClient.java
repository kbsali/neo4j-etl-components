package org.neo4j.integration.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.neo4j.integration.io.AwaitHandle;
import org.neo4j.integration.util.FutureUtils;
import org.neo4j.integration.util.Loggers;

public class DatabaseClient implements AutoCloseable
{
    private final Connection connection;

    public DatabaseClient( ConnectionConfig connectionConfig ) throws SQLException, ClassNotFoundException
    {
        Loggers.Sql.log().fine( "Connecting to database..." );

        Class.forName( connectionConfig.driverClassName() );

        connection = DriverManager.getConnection(
                connectionConfig.uri().toString(),
                connectionConfig.username(),
                connectionConfig.password() );

        Loggers.Sql.log().fine( "Connected to database" );
    }

    public AwaitHandle<QueryResults> executeQuery( String sql )
    {
        return new DatabaseClientAwaitHandle<>(
                FutureUtils.<QueryResults>exceptionableFuture( () ->
                {
                    Loggers.Sql.log().finest( sql );
                    return new SqlQueryResults( connection.createStatement().executeQuery( sql ) );

                }, r -> new Thread( r ).start() ) );
    }

    public AwaitHandle<Boolean> execute( String sql )
    {
        return new DatabaseClientAwaitHandle<>(
                FutureUtils.exceptionableFuture( () ->
                {
                    Loggers.Sql.log().finest( sql );
                    return connection.createStatement().execute( sql );

                }, r -> new Thread( r ).start() ) );
    }

    @Override
    public void close() throws Exception
    {
        connection.close();
    }

    private static class DatabaseClientAwaitHandle<T> implements AwaitHandle<T>
    {
        private final CompletableFuture<T> future;

        private DatabaseClientAwaitHandle( CompletableFuture<T> future )
        {
            this.future = future;
        }

        @Override
        public T await() throws Exception
        {
            return future.get();
        }

        @Override
        public T await( long timeout, TimeUnit unit ) throws Exception
        {
            return future.get( timeout, unit );
        }

        @Override
        public CompletableFuture<T> toFuture()
        {
            return future;
        }
    }

    private static class SqlQueryResults implements QueryResults
    {
        private final ResultSet results;

        public SqlQueryResults( ResultSet results )
        {
            this.results = results;
        }

        @Override
        public boolean next() throws Exception
        {
            return results.next();
        }

        @Override
        public String getString( String columnLabel ) throws Exception
        {
            return results.getString( columnLabel );
        }

        @Override
        public void close() throws Exception
        {
            results.close();
        }
    }
}