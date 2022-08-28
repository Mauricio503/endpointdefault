package tech.criasystem.service;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import tech.criasystem.model.Tenant;
import tech.criasystem.multitenancy.DDLExporter;
import tech.criasystem.multitenancy.NoTablesCreatedException;
import tech.criasystem.multitenancy.SQLExportCommand;

@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class SchemaDDLService {
	private static final Logger LOGGER = LoggerFactory.getLogger(SchemaDDLService.class);
	private static final String templateCreateSchema = "CREATE SCHEMA %s";
	private static final String templateAlterSchema = "SET SCHEMA '%s'";
	private static final String templateDeleteSchema = "DROP SCHEMA %s CASCADE";
	public static final String schemaPublic = "public";
	private static final String templateSelectSchemas = "select schema_name from information_schema.schemata";
	private static final String templateSelectCountTables = "SELECT count(*) as count FROM information_schema.tables WHERE table_schema = ?";
	private static final String schameNameField = "schema_name";

	@Autowired
	private DataSource dataSource;
	@Autowired
	private DDLExporter ddlExporter;

	public void exportSchema(Tenant entidade) throws SQLException {
		Connection connection = dataSource.getConnection();
		Statement stmt = connection.createStatement();
		try {
			final String schema = entidade.getSchema();
			

			List<SQLExportCommand> commands = ddlExporter.exportCreate(schema);

			SQLExportCommand sqlAddGeom = new SQLExportCommand("CREATE EXTENSION postgis");
			commands.add(sqlAddGeom);
			
			boolean existeSchema = isSchemaPresent(schema, connection);

			if (!existeSchema) {
				LOGGER.info("Creating schema " + schema);
				stmt.execute(String.format(templateCreateSchema, schema));
				LOGGER.info("Schema " + schema + " created.");
			}
			
			exportTables(schema, connection, commands);

			boolean tablesCreated = containsTablesInSchema(schema, connection);
			if (!tablesCreated) {
				throw new NoTablesCreatedException("Nao foi possivel criar as tabelas no novo schema.");
			}
		} finally {
			if (stmt != null) {
				stmt.close();
			}
			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void updateSchema(Tenant entidade) throws SQLException {
		Connection connection = dataSource.getConnection();
		try {
			final String schema = entidade.getSchema();

			List<SQLExportCommand> commands = ddlExporter.exportUpdate(connection, schema);

			boolean existeSchema = isSchemaPresent(schema, connection);

			if (!existeSchema) {
				LOGGER.info("Updating schema " + schema);
				try (Statement stmt = connection.createStatement()){
					stmt.execute(String.format(templateCreateSchema, schema));
				}
				LOGGER.info("Schema " + schema + " updated.");
			}

			exportTables(schema, connection, commands);

			boolean tablesCreated = containsTablesInSchema(schema, connection);
			if (!tablesCreated) {
				throw new NoTablesCreatedException("Nao foi possivel criar as tabelas no novo schema.");
			}
		} finally {
			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void deleteSchema(Tenant entidade) throws SQLException {
		final String schema = entidade.getSchema();
		Connection connection = dataSource.getConnection();
		try {
			boolean existeSchema = isSchemaPresent(schema, connection);

			if (existeSchema) {
				LOGGER.info("Creating schema " + schema);
				try(Statement stmt = connection.createStatement()){
					stmt.execute(String.format(templateDeleteSchema, schema));
				}
				LOGGER.info("Schema " + schema + " excluido.");
			}
		} finally {
			connection.close();
		}

	}

	public void exportTables(final String schema, Connection connection, List<SQLExportCommand> commands) throws SQLException {
		LOGGER.info("Creating tables for " + schema);
		try (Statement stmt = connection.createStatement()){
			stmt.execute(String.format(templateAlterSchema, schema));
		}
		for (SQLExportCommand sqlExportCommand : commands) {
			LOGGER.info(sqlExportCommand.toString());
			try {
				try(Statement stmt = connection.createStatement()) {
					if(sqlExportCommand.getSqlCommand().contains("geom")) {
						System.out.println("contem geom");
					}
					if(sqlExportCommand.getSqlCommand().contains("postgis")) {
						System.out.println("contem postgis");
					}
					stmt.execute(sqlExportCommand.getSqlCommand());
				}
			} catch (SQLException e) {
				LOGGER.error("Error", e);
			}
		}
		try(Statement stmt = connection.createStatement()){
			stmt.execute(String.format(templateAlterSchema, schemaPublic));
		}
		LOGGER.info("Created tables for " + schema);
	}

	public void exportPublicSchema() throws SQLException {
		Tenant entidade = new Tenant() {
			private static final long serialVersionUID = 1L;

			@Override
			public String getSchema() {
				return schemaPublic;
			}
		};
		exportSchema(entidade);
		updateSchema(entidade);
	}

	public boolean isSchemaPresent(String schemaName, Connection connection) throws SQLException {
		try(ResultSet resultSet = connection.prepareStatement(templateSelectSchemas).executeQuery()){
			while (resultSet.next()) {
				if (resultSet.getString(schameNameField).equalsIgnoreCase(schemaName)) {
					return true;
				}
			}
			return false;
		}
	}

	public boolean containsTablesInSchema(String schemaName, Connection connection) throws SQLException {
		PreparedStatement statement = connection.prepareStatement(templateSelectCountTables);
		statement.setString(1, schemaName);
		
		try(ResultSet resultSet = statement.executeQuery()){
			while (resultSet.next()) {
				long count = resultSet.getLong("count");
				if (count != 0) {
					return true;
				}
			}
			return false;
		} finally {
			statement.close();
		}
	}
}
