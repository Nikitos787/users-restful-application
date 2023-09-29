package back.service.impl;

import back.dto.BirthDateBetweenSearchParametersDto;
import back.dto.UserRegistrationRequestDto;
import back.dto.UserRequestDto;
import back.dto.UserResponseDto;
import back.dto.UserSearchParametersDto;
import back.exception.EntityNotFoundException;
import back.exception.OperationException;
import back.exception.RegistrationException;
import back.mapper.UserMapper;
import back.model.Role;
import back.model.User;
import back.repository.SpecificationBuilder;
import back.repository.user.UserRepository;
import back.service.RoleService;
import back.service.UserService;
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
        isUserWithEmailAlreadyExistInDb(userRegistrationRequestDto.getEmail());
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
        isUserWithEmailAlreadyExistInDb(userRequestDto.getEmail());
        isUserCanUpdateInfo(id, authenticatedUser);

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
        isUserCanUpdateInfo(userId, authenticatedUser);
        User user = getUserById(userId);
        User updatedUser = applyPatchToUser(jsonPatch, user);
        if (!user.getEmail().equals(updatedUser.getEmail())) {
            isUserWithEmailAlreadyExistInDb(updatedUser.getEmail());
        }
        return userMapper.toDto(userRepository.save(updatedUser));
    }

    @Override
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    private void isUserWithEmailAlreadyExistInDb(String email) throws RegistrationException {
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

    private void isUserCanUpdateInfo(Long id,
                                     User authenticatedUser) {
        if (!authenticatedUser.getId().equals(id)) {
            throw new OperationException("Cannot update another user's info");
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
    }

    private User createUserForSave(UserRegistrationRequestDto userRegistrationRequestDto) {
        User user = userMapper.toModel(userRegistrationRequestDto);
        userRegistrationRequestDto.setPassword(
                passwordEncoder.encode(userRegistrationRequestDto.getPassword())
        );
        user.setRoles(Set.of(roleService.findByRoleName(Role.RoleName.ROLE_USER)));
        return user;
    }
}
