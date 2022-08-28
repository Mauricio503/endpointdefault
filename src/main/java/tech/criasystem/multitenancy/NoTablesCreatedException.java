package tech.criasystem.multitenancy;

import java.sql.SQLException;

public class NoTablesCreatedException extends SQLException {

	private static final long serialVersionUID = -7566654212249035671L;

	public NoTablesCreatedException(String message) {
		super(message);
	}

	public NoTablesCreatedException(Throwable throwable) {
		super(throwable);
	}
}
