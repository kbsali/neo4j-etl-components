package org.neo4j.integration.sql.exportcsv.mysql;

import java.nio.file.Path;
import java.util.Collection;

import org.neo4j.integration.neo4j.importcsv.HeaderFileWriter;
import org.neo4j.integration.neo4j.importcsv.config.GraphDataConfigSupplier;
import org.neo4j.integration.neo4j.importcsv.config.NodeConfig;
import org.neo4j.integration.sql.exportcsv.ExportDatabaseContentsToCsv;
import org.neo4j.integration.sql.exportcsv.ExportFileWriter;
import org.neo4j.integration.sql.exportcsv.config.ExportToCsvConfig;
import org.neo4j.integration.sql.exportcsv.mapping.TableMapper;
import org.neo4j.integration.sql.metadata.Table;

class ExportMySqlTable
{
    private final Table table;
    private final HeaderFileWriter headerFileWriter;
    private final ExportFileWriter exportFileWriter;
    private final ExportToCsvConfig config;

    ExportMySqlTable( Table table,
                      HeaderFileWriter headerFileWriter,
                      ExportFileWriter exportFileWriter,
                      ExportToCsvConfig config )
    {
        this.table = table;
        this.headerFileWriter = headerFileWriter;
        this.exportFileWriter = exportFileWriter;
        this.config = config;
    }

    public GraphDataConfigSupplier export() throws Exception
    {
        Collection<Path> files =
                new ExportDatabaseContentsToCsv<Table>( headerFileWriter, exportFileWriter )
                        .execute( table,
                                new TableMapper( config.formatting() ),
                                new MySqlTableExportSqlSupplier( config.formatting() ) );

        return NodeConfig.builder()
                .addInputFiles( files )
                .addLabel( table.name().simpleName() )
                .build();
    }
}