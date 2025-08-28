package com.crediya.api.docs;

import com.crediya.api.dto.request.CreateUserRequestDto;
import com.crediya.api.dto.request.UpdateUserRequestDto;
import com.crediya.api.dto.response.UserResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Users", description = "Operaciones relacionadas con usuarios")
public class UserApiDocs {

    @Operation(
        summary = "Crear usuario",
        description = "Crea un nuevo usuario en el sistema",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Datos del usuario a crear",
            required = true,
            content = @Content(schema = @Schema(implementation = CreateUserRequestDto.class))
        ),
        responses = {
            @ApiResponse(
                responseCode = "200", 
                description = "Usuario creado exitosamente",
                content = @Content(schema = @Schema(implementation = UserResponseDto.class))
            ),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "409", description = "El email ya está registrado")
        }
    )
    public void createUser() {}

    @Operation(
        summary = "Obtener usuario por ID",
        description = "Obtiene un usuario específico por su ID",
        parameters = @Parameter(
            name = "id", 
            in = ParameterIn.PATH, 
            description = "ID del usuario", 
            required = true,
            schema = @Schema(type = "integer", format = "int64")
        ),
        responses = {
            @ApiResponse(
                responseCode = "200", 
                description = "Usuario encontrado",
                content = @Content(schema = @Schema(implementation = UserResponseDto.class))
            ),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
        }
    )
    public void getUserById() {}

    @Operation(
        summary = "Buscar usuario por email",
        description = "Busca un usuario por su dirección de correo electrónico",
        parameters = @Parameter(
            name = "email", 
            in = ParameterIn.QUERY, 
            description = "Email del usuario a buscar", 
            required = true,
            schema = @Schema(type = "string", format = "email")
        ),
        responses = {
            @ApiResponse(
                responseCode = "200", 
                description = "Usuario encontrado",
                content = @Content(schema = @Schema(implementation = UserResponseDto.class))
            ),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "400", description = "Email inválido")
        }
    )
    public void getUserByEmail() {}

    @Operation(
        summary = "Actualizar usuario",
        description = "Actualiza los datos de un usuario existente",
        parameters = @Parameter(
            name = "id", 
            in = ParameterIn.PATH, 
            description = "ID del usuario a actualizar", 
            required = true,
            schema = @Schema(type = "integer", format = "int64")
        ),
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Nuevos datos del usuario",
            required = true,
            content = @Content(schema = @Schema(implementation = UpdateUserRequestDto.class))
        ),
        responses = {
            @ApiResponse(
                responseCode = "200", 
                description = "Usuario actualizado exitosamente",
                content = @Content(schema = @Schema(implementation = UserResponseDto.class))
            ),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "409", description = "El email ya está registrado por otro usuario")
        }
    )
    public void updateUser() {}

    @Operation(
        summary = "Eliminar usuario",
        description = "Elimina un usuario del sistema",
        parameters = @Parameter(
            name = "id", 
            in = ParameterIn.PATH, 
            description = "ID del usuario a eliminar", 
            required = true,
            schema = @Schema(type = "integer", format = "int64")
        ),
        responses = {
            @ApiResponse(responseCode = "204", description = "Usuario eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
        }
    )
    public void deleteUser() {}

    @Operation(
        summary = "Listar todos los usuarios",
        description = "Obtiene una lista de todos los usuarios del sistema",
        responses = {
            @ApiResponse(
                responseCode = "200", 
                description = "Lista de usuarios obtenida exitosamente",
                content = @Content(schema = @Schema(implementation = UserResponseDto[].class))
            )
        }
    )
    public void getAllUsers() {}
}