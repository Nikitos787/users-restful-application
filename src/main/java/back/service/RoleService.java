package back.service;

import back.model.Role;

public interface RoleService {
    Role save(Role role);

    Role findByRoleName(Role.RoleName roleName);
}
