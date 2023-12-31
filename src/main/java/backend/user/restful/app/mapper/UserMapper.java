package backend.user.restful.app.mapper;

import backend.user.restful.app.config.MapperConfig;
import backend.user.restful.app.dto.UserRegistrationRequestDto;
import backend.user.restful.app.dto.UserResponseDto;
import backend.user.restful.app.model.User;
import java.util.stream.Collectors;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    @Mapping(source = "birthdate", target = "birthdate")
    UserResponseDto toDto(User user);

    @Mapping(ignore = true, target = "id")
    @Mapping(ignore = true, target = "roles")
    @Mapping(ignore = true, target = "authorities")
    @Mapping(ignore = true, target = "deleted")
    User toModel(UserRegistrationRequestDto userRegistrationRequestDto);

    @AfterMapping
    default void setRolesName(@MappingTarget UserResponseDto userResponseDto, User user) {
        userResponseDto.setRolesName(user.getRoles().stream()
                .map(role -> role.getRoleName().name())
                .collect(Collectors.toSet()));
    }
}
