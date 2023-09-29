package back.service;

import back.dto.BirthDateBetweenSearchParametersDto;
import back.dto.UserRegistrationRequestDto;
import back.dto.UserRequestDto;
import back.dto.UserResponseDto;
import back.dto.UserSearchParametersDto;
import back.exception.RegistrationException;
import back.model.User;
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
