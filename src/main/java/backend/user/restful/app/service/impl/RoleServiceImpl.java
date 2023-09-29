package backend.user.restful.app.service.impl;

import backend.user.restful.app.exception.EntityNotFoundException;
import backend.user.restful.app.model.Role;
import backend.user.restful.app.repository.RoleRepository;
import backend.user.restful.app.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;

    @Override
    public Role save(Role role) {
        return roleRepository.save(role);
    }

    @Override
    public Role findByRoleName(Role.RoleName roleName) {
        return roleRepository.findByRoleName(roleName).orElseThrow(() ->
                new EntityNotFoundException(String
                        .format("Role with such name: %s does not exist in DB", roleName.name())));
    }
}
