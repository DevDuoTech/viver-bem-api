package br.com.devduo.viverbemapi.utils.evaluator;

import br.com.devduo.viverbemapi.dtos.TenantsRequestDTO;
import br.com.devduo.viverbemapi.enums.RoleEnum;
import br.com.devduo.viverbemapi.models.Contract;
import br.com.devduo.viverbemapi.models.Role;
import br.com.devduo.viverbemapi.models.Tenant;
import br.com.devduo.viverbemapi.models.User;
import br.com.devduo.viverbemapi.repository.TenantRepository;
import br.com.devduo.viverbemapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.Serializable;


@Component
public class CustomPermissionEvaluator implements PermissionEvaluator {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TenantRepository tenantRepository;

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        if (authentication == null || !authentication.isAuthenticated() || targetDomainObject == null || !(permission instanceof String)) {
            return false;
        }

        User loggedUser = getLoggedUser(authentication);
        if (loggedUser == null) {
            return false;
        }

        return switch ((String) permission) {
            // User
            case "UPDATE_PROFILE_PICTURE" -> hasPermission(loggedUser, targetDomainObject);
            // Contract
            case "FIND_ALL_CONTRACTS" -> hasPermission(loggedUser, targetDomainObject);
            case "GET_CONTRACT_BY_UUID" -> hasContractPermission(loggedUser, targetDomainObject);
            // Tenant
            case "UPDATE_TENANT" -> hasUpdateTenantPermission(loggedUser, targetDomainObject);
            case "FIND_TENANT_BY_ID" -> hasPermission(loggedUser, targetDomainObject);
            default -> false;
        };
    }

    private boolean hasPermission(User loggedUser, Object targetTenantId) {
        for (Role role : loggedUser.getRoles()) {
            if (role.getDescription().equals(RoleEnum.USER)) {
                Long userTenantId = loggedUser.getTenantId();
                if (targetTenantId instanceof Long) {
                    return userTenantId.equals(targetTenantId);
                }
                return false;
            }
        }
        return false;
    }

    private boolean hasUpdateTenantPermission(User loggedUser, Object targetDomainObject) {
        TenantsRequestDTO tenantsRequestDTO = (TenantsRequestDTO) targetDomainObject;
        for (Role role : loggedUser.getRoles()) {
            if (role.getDescription().equals(RoleEnum.USER)) {
                Tenant tenant = tenantRepository.findByEmail(tenantsRequestDTO.getEmail()).orElse(null);
                if (tenant != null) {
                    Long userTenantId = loggedUser.getTenantId();
                    return userTenantId.equals(tenant.getId());
                }
                return false;
            }
        }
        return false;
    }

    private boolean hasContractPermission(User loggedUser, Object targetDomainObject) {
        ResponseEntity<Contract> response = (ResponseEntity<Contract>) targetDomainObject;
        if (response.getBody() == null || response.getBody() == null) {
            return false;
        }

        for (Role role : loggedUser.getRoles()) {
            if (role.getDescription().equals(RoleEnum.USER)) {
                Long userTenantId = loggedUser.getTenantId();
                Tenant tenant = response.getBody().getTenant();
                return tenant.getId().equals(userTenantId);
            }
        }
        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        return false;
    }

    private User getLoggedUser(Authentication authentication) {
        Object principal = authentication.getPrincipal();

        if (principal instanceof User) {
            return (User) principal;
        } else if (principal instanceof UserDetails userDetails) {
            return userRepository.findByEmail(userDetails.getUsername());
        }

        return null;
    }
}
