package tech.criasystem.multitenancy;


import org.hibernate.context.spi.CurrentTenantIdentifierResolver;

public class MultitenantSchemaResolver implements CurrentTenantIdentifierResolver {

	@Override
	public String resolveCurrentTenantIdentifier() {
		String schema = UserLoginLogadoUtils.getCurrentSchema();
		return schema;
	}

	@Override
	public boolean validateExistingCurrentSessions() {
		return false;
	}

}