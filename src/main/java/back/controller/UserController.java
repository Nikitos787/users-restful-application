package back.controller;

import back.dto.BirthDateBetweenSearchParametersDto;
import back.dto.UserRequestDto;
import back.dto.UserResponseDto;
import back.dto.UserSearchParametersDto;
import back.exception.RegistrationException;
import back.model.User;
import back.service.UserService;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User management", description = "endpoints for managing users")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private static final String USER = "ROLE_USER";
    private static final String ADMIN = "ROLE_ADMIN";

    private final UserService userService;

    @GetMapping
    @Secured(USER)
    public List<UserResponseDto> findAll(Pageable pageable) {
        return userService.findAll(pageable);
    }

    @GetMapping("/search")
    @Operation(summary = "Get users by parameters")
    @Secured(USER)
    public List<UserResponseDto> searchUsers(
            @Parameter(schema = @Schema(
                    implementation = UserSearchParametersDto.class)
            )
            UserSearchParametersDto userSearchParametersDto) {
        return userService.search(userSearchParametersDto);
    }

    @GetMapping("/search-by-date-between")
    @Operation(summary = "Get users by date between")
    @Secured(USER)
    public List<UserResponseDto> searchByDate(
            @Parameter(schema = @Schema(
                    implementation = BirthDateBetweenSearchParametersDto.class)
            )
            @Valid BirthDateBetweenSearchParametersDto searchParametersDto) {
        return userService.searchByBirthDateBetween(searchParametersDto);
    }

    @GetMapping("/{id}")
    @Secured(ADMIN)
    public UserResponseDto findById(@PathVariable Long id) {
        return userService.findById(id);
    }

    @Operation(summary = "Update all user info")
    @PutMapping("/{id}")
    @Secured(USER)
    public UserResponseDto updateAllInfo(@PathVariable Long id,
                                         @RequestBody UserRequestDto userRequestDto,
                                         Authentication authentication)
            throws RegistrationException {
        UserDetails principal = (UserDetails) authentication.getPrincipal();
        User authenticatedUser = userService.findByEmail(principal.getUsername());
        return userService.updateInfo(id, authenticatedUser, userRequestDto);
    }

    @Operation(summary = "Update some user info")
    @PatchMapping(path = "/{id}", consumes = "application/json-patch+json")
    @Secured(USER)
    public UserResponseDto patch(@PathVariable Long id,
                                 @RequestBody JsonPatch patchDocument,
                                 Authentication authentication)
            throws JsonPatchException, IOException, RegistrationException {
        UserDetails principal = (UserDetails) authentication.getPrincipal();
        User authenticatedUser = userService.findByEmail(principal.getUsername());
        return userService.patch(id, authenticatedUser, patchDocument);
    }

    @Operation(summary = "Delete user by id. Note: Here impleneted safe delete")
    @DeleteMapping("/{id}")
    @Secured(ADMIN)
    public void delete(@PathVariable Long id) {
        userService.delete(id);
    }
}
