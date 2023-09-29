package backend.user.restful.app.service.impl;

import backend.user.restful.app.dto.BirthDateBetweenSearchParametersDto;
import backend.user.restful.app.dto.UserRegistrationRequestDto;
import backend.user.restful.app.dto.UserRequestDto;
import backend.user.restful.app.dto.UserResponseDto;
import backend.user.restful.app.dto.UserSearchParametersDto;
import backend.user.restful.app.exception.EntityNotFoundException;
import backend.user.restful.app.exception.RegistrationException;
import backend.user.restful.app.exception.UserOperationException;
import backend.user.restful.app.mapper.UserMapper;
import backend.user.restful.app.model.Role;
import backend.user.restful.app.model.User;
import backend.user.restful.app.repository.SpecificationBuilder;
import backend.user.restful.app.repository.user.UserRepository;
import backend.user.restful.app.service.RoleService;
import backend.user.restful.app.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final RoleService roleService;
    private final SpecificationBuilder<User> userSpecificationBuilder;
    private final ObjectMapper objectMapper;

    @Override
    public UserResponseDto save(UserRegistrationRequestDto userRegistrationRequestDto)
            throws RegistrationException {
        checkIfUserExistInDb(userRegistrationRequestDto.getEmail());
        User user = createUserForSave(userRegistrationRequestDto);
        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    public List<UserResponseDto> search(UserSearchParametersDto userSearchParametersDto) {
        Specification<User> specification = userSpecificationBuilder.build(userSearchParametersDto);
        return userRepository.findAll(specification).stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Override
    public UserResponseDto findById(Long id) {
        return userMapper.toDto(getUserById(id));
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() ->
                new EntityNotFoundException(String
                        .format("Can't find user with email: %s in DB", email)));
    }

    @Override
    public List<UserResponseDto> findAll(Pageable pageable) {
        return userRepository.findAll(pageable).stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Override
    public List<UserResponseDto> searchByBirthDateBetween(
            BirthDateBetweenSearchParametersDto birthDateBetweenSearchParametersDto
    ) {
        return userRepository.findAllByBirthdateBetween(
                        birthDateBetweenSearchParametersDto.getFrom(),
                        birthDateBetweenSearchParametersDto.getTo()
                ).stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Override
    public UserResponseDto updateInfo(Long id,
                                      User authenticatedUser,
                                      UserRequestDto userRequestDto) throws RegistrationException {
        checkIfUserExistInDb(userRequestDto.getEmail());
        checkUserCanUpdateInfo(id, authenticatedUser);

        User userForUpdate = getUserById(id);
        updateUserFields(userForUpdate, userRequestDto);

        return userMapper.toDto(userRepository.save(userForUpdate));
    }

    @Override
    public UserResponseDto patch(Long userId,
                                 User authenticatedUser,
                                 JsonPatch jsonPatch)
            throws JsonPatchException,
            JsonProcessingException,
            RegistrationException {
        checkUserCanUpdateInfo(userId, authenticatedUser);
        User user = getUserById(userId);
        User updatedUser = applyPatchToUser(jsonPatch, user);
        if (!user.getEmail().equals(updatedUser.getEmail())) {
            checkIfUserExistInDb(updatedUser.getEmail());
        }
        return userMapper.toDto(userRepository.save(updatedUser));
    }

    @Override
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    private void checkIfUserExistInDb(String email) throws RegistrationException {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RegistrationException(
                    String.format("User with such email: %s already exist in db",
                            email));
        }
    }

    private User applyPatchToUser(
            JsonPatch patch, User targetUser) throws JsonPatchException, JsonProcessingException {
        JsonNode patched = patch.apply(objectMapper.convertValue(targetUser, JsonNode.class));
        return objectMapper.treeToValue(patched, User.class);
    }

    private void checkUserCanUpdateInfo(Long id,
                                        User authenticatedUser) {
        if (!authenticatedUser.getId().equals(id)) {
            throw new UserOperationException("Cannot update another user's info");
        }
    }

    private User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException(
                        String.format("Can't find user by user id: %s", id)));
    }

    private void updateUserFields(User user, UserRequestDto userRequestDto) {
        user.setFirstName(userRequestDto.getFirstName());
        user.setLastName(userRequestDto.getLastName());
        user.setEmail(userRequestDto.getEmail());
        user.setPassword(passwordEncoder.encode(userRequestDto.getPassword()));
        user.setBirthdate(userRequestDto.getBirthdate());
        user.setPhoneNumber(userRequestDto.getPhoneNumber());
        user.setAddress(userRequestDto.getAddress());
    }

    private User createUserForSave(UserRegistrationRequestDto userRegistrationRequestDto) {
        User user = userMapper.toModel(userRegistrationRequestDto);
        String encodePassword = passwordEncoder.encode(userRegistrationRequestDto.getPassword());
        user.setPassword(encodePassword);
        user.setRoles(Set.of(roleService.findByRoleName(Role.RoleName.ROLE_USER)));
        return user;
    }
}
