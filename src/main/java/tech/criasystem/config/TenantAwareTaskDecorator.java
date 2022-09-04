package tech.criasystem.config;


import org.springframework.core.task.TaskDecorator;
import org.springframework.lang.NonNull;

import tech.criasystem.multitenancy.UserLoginLogadoUtils;
import tech.criasystem.multitenancy.UserLoginMultitenancy;

public class TenantAwareTaskDecorator implements TaskDecorator {

    @Override
    @NonNull
    public Runnable decorate(@NonNull Runnable runnable) {
        UserLoginMultitenancy userMultitenancy = UserLoginLogadoUtils.getUserLoginMultitenancyLogado();
        return () -> {
            try {
            	UserLoginLogadoUtils.adicionarNoContexto(userMultitenancy);
                runnable.run();
            } finally {
            	UserLoginLogadoUtils.adicionarNoContexto(null);
            }
        };
    }
}