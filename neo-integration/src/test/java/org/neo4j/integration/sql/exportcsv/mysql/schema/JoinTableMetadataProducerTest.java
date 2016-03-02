package org.neo4j.integration.sql.exportcsv.mysql.schema;

import java.util.Collection;
import java.util.Iterator;

import org.junit.Test;

import org.neo4j.integration.io.AwaitHandle;
import org.neo4j.integration.sql.DatabaseClient;
import org.neo4j.integration.sql.QueryResults;
import org.neo4j.integration.sql.StubQueryResults;
import org.neo4j.integration.sql.metadata.Column;
import org.neo4j.integration.sql.metadata.ColumnType;
import org.neo4j.integration.sql.metadata.JoinTable;
import org.neo4j.integration.sql.metadata.JoinTableInfo;
import org.neo4j.integration.sql.metadata.SqlDataType;
import org.neo4j.integration.sql.metadata.TableName;
import org.neo4j.integration.sql.metadata.TableNamePair;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JoinTableMetadataProducerTest
{
    @Test
    public void shouldReturnJoinTableMetadata() throws Exception
    {
        // given
        QueryResults results = StubQueryResults.builder()
                .columns( "TABLE_SCHEMA",
                        "TABLE_NAME",
                        "FOREIGN_KEY",
                        "REFERENCED_PRIMARY_KEY",
                        "REFERENCED_TABLE_SCHEMA",
                        "REFERENCED_TABLE_NAME" )
                .addRow( "test", "Student_Course", "studentId", "id", "test", "Student" )
                .addRow( "test", "Student_Course", "courseId", "id", "test", "Course" )
                .build();

        DatabaseClient databaseClient = mock( DatabaseClient.class );
        when( databaseClient.executeQuery( any( String.class ) ) ).thenReturn( AwaitHandle.forReturnValue( results ) );

        JoinTableMetadataProducer metadataProducer = new JoinTableMetadataProducer( databaseClient );

        // when
        Collection<JoinTable> joinTables = metadataProducer
                .createMetadataFor( new JoinTableInfo( new TableName( "test.Student_Course" ), new TableNamePair(
                        new TableName( "test.Student" ),
                        new TableName( "test.Course" ) ) ) );

        // then
        Iterator<JoinTable> iterator = joinTables.iterator();

        JoinTable joinTable = iterator.next();

        assertEquals( Column.builder()
                        .table( new TableName( "test.Student_Course" ) )
                        .name( "test.Student_Course.studentId" )
                        .alias( "studentId" )
                        .columnType( ColumnType.ForeignKey )
                        .dataType( SqlDataType.KEY_DATA_TYPE )
                        .build(),
                joinTable.startForeignKey() );

        assertEquals( Column.builder()
                        .table( new TableName( "test.Student" ) )
                        .name( "test.Student.id" )
                        .alias( "id" )
                        .columnType( ColumnType.PrimaryKey )
                        .dataType( SqlDataType.KEY_DATA_TYPE )
                        .build(),
                joinTable.startPrimaryKey() );

        assertEquals( Column.builder()
                        .table( new TableName( "test.Student_Course" ) )
                        .name( "test.Student_Course.courseId" )
                        .alias( "courseId" )
                        .columnType( ColumnType.ForeignKey )
                        .dataType( SqlDataType.KEY_DATA_TYPE )
                        .build(),
                joinTable.endForeignKey() );

        assertEquals( Column.builder()
                        .table( new TableName( "test.Course" ) )
                        .name( "test.Course.id" )
                        .alias( "id" )
                        .columnType( ColumnType.PrimaryKey )
                        .dataType( SqlDataType.KEY_DATA_TYPE )
                        .build(),
                joinTable.endPrimaryKey() );

        assertFalse( iterator.hasNext() );
    }
}