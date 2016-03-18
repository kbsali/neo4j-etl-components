package org.neo4j.integration.sql.exportcsv.mapping;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import org.junit.Test;

import org.neo4j.integration.neo4j.importcsv.config.Formatting;
import org.neo4j.integration.neo4j.importcsv.fields.CsvField;
import org.neo4j.integration.neo4j.importcsv.fields.IdSpace;
import org.neo4j.integration.sql.exportcsv.TestUtil;
import org.neo4j.integration.sql.metadata.Column;
import org.neo4j.integration.sql.metadata.ColumnType;
import org.neo4j.integration.sql.metadata.Join;
import org.neo4j.integration.sql.metadata.JoinKey;
import org.neo4j.integration.sql.metadata.TableName;

import static java.util.Arrays.asList;

import static org.junit.Assert.assertEquals;

public class JoinToCsvFieldMapperTest
{

    private JoinToCsvFieldMapper mapper = new JoinToCsvFieldMapper( Formatting.DEFAULT );
    private TestUtil testUtil = new TestUtil();

    @Test
    public void shouldCreateMappingsForJoinTable()
    {
        // given
        TableName leftTable = new TableName( "test.Person" );
        TableName rightTable = new TableName( "test.Address" );
        Join join = new Join(
                new JoinKey(
                        testUtil.column( leftTable, "id", ColumnType.PrimaryKey ),
                        testUtil.column( leftTable, "id", ColumnType.PrimaryKey ) ),
                new JoinKey(
                        testUtil.column( leftTable, "addressId", ColumnType.ForeignKey ),
                        testUtil.column( rightTable, "id", ColumnType.PrimaryKey ) )
        );
        // when
        ColumnToCsvFieldMappings mappings = mapper.createMappings( join );

        // then
        Collection<CsvField> fields = new ArrayList<>( mappings.fields() );
        Collection<String> columns = mappings.columns().stream().map( Column::name ).collect( Collectors.toList() );

        assertEquals( fields, asList(
                CsvField.startId( new IdSpace( "test.Person" ) ),
                CsvField.endId( new IdSpace( "test.Address" ) ),
                CsvField.relationshipType() ) );

        assertEquals( asList( "test.Person.id", "test.Person.addressId", "\"ADDRESS\"" ), columns );
    }
}
