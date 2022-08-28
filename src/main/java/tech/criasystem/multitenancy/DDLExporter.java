package tech.criasystem.multitenancy;


import java.sql.Connection;
import java.util.List;

public interface DDLExporter {

	List<SQLExportCommand> exportCreate(String schemaName);

	List<SQLExportCommand> exportUpdate(Connection connection, String schemaName);
}
