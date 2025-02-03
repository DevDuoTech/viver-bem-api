package br.com.devduo.viverbemapi.utils.evaluator;

import br.com.devduo.viverbemapi.enums.RoleEnum;
import br.com.devduo.viverbemapi.models.User;
import br.com.devduo.viverbemapi.repository.ContractRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;


@Component
public class CustomPermissionEvaluator implements PermissionEvaluator {
    @Autowired
    private ContractRepository contractRepository;

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        if (authentication == null || !authentication.isAuthenticated() || targetDomainObject == null || !(permission instanceof String)) {
            return false;
        }

        User loggedUser = (User) authentication.getPrincipal();
        if (loggedUser == null) {
            return false;
        }

        return switch ((String) permission) {
            // Contract
            case "FIND_ALL_CONTRACTS" -> hasContractPermission(loggedUser, targetDomainObject);
            default -> false;
        };
    }

    private boolean hasContractPermission(User loggedUser, Object targetTenantId) {
        if (loggedUser.getRoles().contains(RoleEnum.USER)) {
            Long userTenantId = loggedUser.getTenantId();
            if (targetTenantId instanceof Long) {
                return userTenantId.equals(targetTenantId);
            }
            return false;
        }
        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        return false;
    }
}
