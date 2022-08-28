package tech.criasystem.multitenancy;



public class SQLExportCommand {
	private final String sqlCommand;

	public SQLExportCommand(String sql) {
		this.sqlCommand = sql;
	}

	public String getSqlCommand() {
		return sqlCommand;
	}

	public boolean isCreateTable() {
		return sqlCommand != null && sqlCommand.startsWith("create table if not exists ");
	}

	public boolean isCreateSequence() {
		return sqlCommand != null && sqlCommand.startsWith("create sequence if not exists ");
	}

	public boolean isAlterTable() {
		return sqlCommand != null && sqlCommand.startsWith("alter table ");
	}

	@Override
	public String toString() {

		return new StringBuilder("Export sql command [").append(sqlCommand).append("]").toString();
	}
}
