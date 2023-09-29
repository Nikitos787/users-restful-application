package back.service.impl;

import back.exception.EntityNotFoundException;
import back.model.Role;
import back.repository.RoleRepository;
import back.service.RoleService;
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
