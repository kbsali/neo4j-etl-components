package org.neo4j.integration.sql.exportcsv.mapping;

import org.neo4j.integration.neo4j.importcsv.config.Formatting;
import org.neo4j.integration.neo4j.importcsv.fields.CsvField;
import org.neo4j.integration.neo4j.importcsv.fields.IdSpace;
import org.neo4j.integration.sql.metadata.Column;
import org.neo4j.integration.sql.metadata.ColumnType;
import org.neo4j.integration.sql.metadata.Join;
import org.neo4j.integration.sql.metadata.SqlDataType;

public class JoinToCsvFieldMapper implements DatabaseObjectToCsvFieldMapper<Join>
{
    private final Formatting formatting;

    public JoinToCsvFieldMapper( Formatting formatting )
    {
        this.formatting = formatting;
    }

    @Override
    public ColumnToCsvFieldMappings createMappings( Join join )
    {
        ColumnToCsvFieldMappings.Builder builder = ColumnToCsvFieldMappings.builder();

        builder.add( join.primaryKey(), determineStartOrEndMappingForPrimaryKey( join ) );
        builder.add( join.foreignKey(), determineStartOrEndMappingForForeignKey( join ) );

        String relationshipType = deriveRelationshipType( join ).toUpperCase();

        builder.add(
                Column.builder()
                        .table( join.primaryKey().table() )
                        .name( formatting.quote().enquote( relationshipType ) )
                        .alias( relationshipType )
                        .columnType( ColumnType.Literal )
                        .dataType( SqlDataType.RELATIONSHIP_TYPE_DATA_TYPE )
                        .build(),
                CsvField.relationshipType() );

        return builder.build();
    }

    private String deriveRelationshipType( Join join )
    {
        return join.parentTableRepresentsStartOfRelationship() ?
                join.childTable().simpleName() :
                join.primaryKey().table().simpleName();
    }

    private CsvField determineStartOrEndMappingForPrimaryKey( Join join )
    {
        IdSpace idSpace = new IdSpace( join.primaryKey().table().fullName() );
        return join.parentTableRepresentsStartOfRelationship() ?
                CsvField.startId( idSpace ) :
                CsvField.endId( idSpace );
    }

    private CsvField determineStartOrEndMappingForForeignKey( Join join )
    {
        IdSpace idSpace = new IdSpace( join.childTable().fullName() );
        return join.childTableRepresentsStartOfRelationship() ?
                CsvField.startId( idSpace ) :
                CsvField.endId( idSpace );
    }
}
