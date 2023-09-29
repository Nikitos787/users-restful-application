package backend.user.restful.app.controller;

import backend.user.restful.app.dto.BirthDateBetweenSearchParametersDto;
import backend.user.restful.app.dto.UserRequestDto;
import backend.user.restful.app.dto.UserResponseDto;
import backend.user.restful.app.dto.UserSearchParametersDto;
import backend.user.restful.app.exception.RegistrationException;
import backend.user.restful.app.lib.Constant;
import backend.user.restful.app.model.User;
import backend.user.restful.app.service.UserService;
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
    private final UserService userService;

    @GetMapping
    @Operation(summary = "Get all users from db.",
            description = "You can use pagination and sorting")
    @Secured({Constant.ADMIN, Constant.USER})
    public List<UserResponseDto> findAll(Pageable pageable) {
        return userService.findAll(pageable);
    }

    @GetMapping("/search")
    @Operation(summary = "Get users by parameters")
    @Secured({Constant.ADMIN, Constant.USER})
    public List<UserResponseDto> searchUsers(
            @Parameter(schema = @Schema(
                    implementation = UserSearchParametersDto.class)
            )
            UserSearchParametersDto userSearchParametersDto) {
        return userService.search(userSearchParametersDto);
    }

    @GetMapping("/search-by-date-between")
    @Operation(summary = "Get users by date between")
    @Secured({Constant.ADMIN, Constant.USER})
    public List<UserResponseDto> searchByDate(
            @Parameter(schema = @Schema(
                    implementation = BirthDateBetweenSearchParametersDto.class)
            )
            @Valid BirthDateBetweenSearchParametersDto searchParametersDto) {
        return userService.searchByBirthDateBetween(searchParametersDto);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by id from db.",
            description = "Only for admin")
    @Secured(Constant.ADMIN)
    public UserResponseDto findById(@PathVariable
                                    @Parameter(description = "User id") Long id) {
        return userService.findById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update all user info")
    @Secured({Constant.ADMIN, Constant.USER})
    public UserResponseDto updateAllInfo(@PathVariable
                                         @Parameter(description = "User id") Long id,
                                         @RequestBody
                                         @Valid
                                         @Parameter(schema = @Schema(
                                                 implementation =
                                                         BirthDateBetweenSearchParametersDto.class)
                                         ) UserRequestDto userRequestDto,
                                         Authentication authentication)
            throws RegistrationException {
        User authenticatedUser = getUserFromPrincipal(authentication);
        return userService.updateInfo(id, authenticatedUser, userRequestDto);
    }

    @PatchMapping(path = "/{id}", consumes = "application/json-patch+json")
    @Operation(summary = "Update some user info")
    @Secured({Constant.ADMIN, Constant.USER})
    public UserResponseDto patch(@PathVariable Long id,
                                 @Parameter(description = "User id")
                                 @RequestBody JsonPatch patchDocument,
                                 Authentication authentication)
            throws JsonPatchException, IOException, RegistrationException {
        User authenticatedUser = getUserFromPrincipal(authentication);
        return userService.patch(id, authenticatedUser, patchDocument);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user by id. Note: Here implemented safe delete")
    @Secured(Constant.ADMIN)
    public void delete(@PathVariable
                       @Parameter(description = "User id") Long id) {
        userService.delete(id);
    }

    private User getUserFromPrincipal(Authentication authentication) {
        UserDetails principal = (UserDetails) authentication.getPrincipal();
        return userService.findByEmail(principal.getUsername());
    }
}
