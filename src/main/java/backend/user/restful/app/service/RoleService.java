package backend.user.restful.app.service;

import backend.user.restful.app.model.Role;

public interface RoleService {
    Role save(Role role);

    Role findByRoleName(Role.RoleName roleName);
}
