package backend.user.restful.app.service;

import backend.user.restful.app.dto.BirthDateBetweenSearchParametersDto;
import backend.user.restful.app.dto.UserRegistrationRequestDto;
import backend.user.restful.app.dto.UserRequestDto;
import backend.user.restful.app.dto.UserResponseDto;
import backend.user.restful.app.dto.UserSearchParametersDto;
import backend.user.restful.app.exception.RegistrationException;
import backend.user.restful.app.model.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface UserService {
    List<UserResponseDto> searchByBirthDateBetween(BirthDateBetweenSearchParametersDto
                                                           birthDateBetweenSearchParametersDto);

    UserResponseDto save(UserRegistrationRequestDto userRegistrationRequestDto)
            throws RegistrationException;

    User findByEmail(String email);

    UserResponseDto findById(Long id);

    List<UserResponseDto> findAll(Pageable pageable);

    List<UserResponseDto> search(UserSearchParametersDto userSearchParametersDto);

    UserResponseDto updateInfo(Long id,
                               User authenticatedUser,
                               UserRequestDto userRequestDto) throws RegistrationException;

    UserResponseDto patch(Long userId,
                          User authenticatedUser,
                          JsonPatch jsonPatch)
            throws JsonPatchException,
            JsonProcessingException,
            RegistrationException;

    void delete(Long id);
}
