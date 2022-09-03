package tech.criasystem.multitenancy;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jboss.logging.Logger;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.context.request.RequestContextHolder;

import tech.criasystem.model.Tenant;
import tech.criasystem.model.UserLogin;

public class UserLoginLogadoUtils {
	private static final Logger log = Logger.getLogger(UserLoginLogadoUtils.class.getName());

	public static UserLogin getUserLoginLogado() {
		SecurityContext secureContext = (SecurityContext) SecurityContextHolder.getContext();

		UserLogin UserLogin = null;
		if (secureContext != null) {
			Authentication auth = (Authentication) ((SecurityContext) SecurityContextHolder.getContext()).getAuthentication();

			if (auth != null && auth.getPrincipal() instanceof UserDetails) {
				UserLogin = ((UserLogin) auth.getPrincipal());
			}
		}
		return UserLogin;
	}

	public static Object getPrincipal() {
		SecurityContext secureContext = (SecurityContext) SecurityContextHolder.getContext();

		Object UserLogin = null;
		if (secureContext != null) {
			Authentication auth = (Authentication) ((SecurityContext) SecurityContextHolder.getContext()).getAuthentication();

			if (auth != null && auth.getPrincipal() instanceof UserDetails) {
				UserLogin = auth.getPrincipal();
			}
		}
		return UserLogin;
	}

	public static UserLoginMultitenancy getUserLoginMultitenancyLogado() {
		SecurityContext secureContext = (SecurityContext) SecurityContextHolder.getContext();

		UserLoginMultitenancy UserLogin = null;
		if (secureContext != null) {
			Authentication auth = (Authentication) ((SecurityContext) SecurityContextHolder.getContext()).getAuthentication();

			if (auth != null && auth.getPrincipal() instanceof UserLoginMultitenancy) {
				UserLogin = ((UserLoginMultitenancy) auth.getPrincipal());
			}
		}
		return UserLogin;
	}

	public static String getCurrentSchema() {
		final UserLoginMultitenancy UserLogin = getUserLoginMultitenancyLogado();
		if (UserLogin == null || UserLogin.getSchema() == null) {
			log.warn("UserLogin está null. Retornando schema public...");
			return "public";
		}
		log.info("retornando schema " + UserLogin.getSchema());
		return UserLogin.getSchema();
	}

	public static void adicionarNoContexto(UserLoginMultitenancy UserLoginTenant) {
		Authentication auth = new UsernamePasswordAuthenticationToken(UserLoginTenant, "***");
		SecurityContext sc = new SecurityContextImpl();
		sc.setAuthentication(auth);
		SecurityContextHolder.setContext(sc);
	}

	public static void adicionarNoContexto(UserLoginMultitenancy UserLoginTenant, Collection<GrantedAuthority> authority) {
		Authentication auth = new UsernamePasswordAuthenticationToken(UserLoginTenant, "***", authority);
		SecurityContext sc = new SecurityContextImpl();
		sc.setAuthentication(auth);
		SecurityContextHolder.setContext(sc);
	}

	public static void removerDoContexto() {
		SecurityContextHolder.clearContext();
	}

	public static UserLoginMultitenancy mockUserLoginMultitenancy(final Tenant entidade) {
		return new UserLoginMultitenancy() {

			@Override
			public String getSchema() {
				return entidade.getSchema();
			}
		};
	}

	public static void mockUserLoginAndAddInContext(final Tenant entidade) {
		adicionarNoContexto(mockUserLoginMultitenancy(entidade));
	}

	public static List<UserLogin> getTodosUserLoginsLogados(SessionRegistry sessionRegistry) {
		List<Object> principals = sessionRegistry.getAllPrincipals();
		List<UserLogin> UserLogins = new ArrayList<>();
		for (Object principal : principals) {
			if (principal instanceof UserLogin) {
				UserLogin u = (UserLogin) principal;
				UserLogins.add(u);
			}
		}
		return UserLogins;
	}

	public static Integer getQtdUserLoginsByEntidade(SessionRegistry sessionRegistry, Tenant entidade) {
		Integer nUserLogins = 0;
		for (UserLogin UserLogin : getTodosUserLoginsLogados(sessionRegistry)) {
			if (UserLogin.getTenant() != null && UserLogin.getTenant().getId() != null
				&& entidade.getId().equals(UserLogin.getTenant().getId())) {
					nUserLogins++;
			}
		}
		return nUserLogins;
	}

	public static String getSessionId() {
		return RequestContextHolder.currentRequestAttributes().getSessionId();
	}

}
