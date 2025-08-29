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

@Tag(name = "Users", description = "Operations related to users")
public class UserApiDocs {

    @Operation(
        summary = "Create user",
        description = "Creates a new user in the system",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "User data to create",
            required = true,
            content = @Content(schema = @Schema(implementation = CreateUserRequestDto.class))
        ),
        responses = {
            @ApiResponse(
                responseCode = "200", 
                description = "User created successfully",
                content = @Content(schema = @Schema(implementation = UserResponseDto.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "409", description = "Email is already registered")
        }
    )
    public void createUser() {}

    @Operation(
        summary = "Get user by ID",
        description = "Retrieves a specific user by their ID",
        parameters = @Parameter(
            name = "id", 
            in = ParameterIn.PATH, 
            description = "User ID", 
            required = true,
            schema = @Schema(type = "integer", format = "int64")
        ),
        responses = {
            @ApiResponse(
                responseCode = "200", 
                description = "User found",
                content = @Content(schema = @Schema(implementation = UserResponseDto.class))
            ),
            @ApiResponse(responseCode = "404", description = "User not found")
        }
    )
    public void getUserById() {}

    @Operation(
        summary = "Search user by email",
        description = "Searches for a user by their email address",
        parameters = @Parameter(
            name = "email", 
            in = ParameterIn.QUERY, 
            description = "Email of the user to search", 
            required = true,
            schema = @Schema(type = "string", format = "email")
        ),
        responses = {
            @ApiResponse(
                responseCode = "200", 
                description = "User found",
                content = @Content(schema = @Schema(implementation = UserResponseDto.class))
            ),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "400", description = "Invalid email")
        }
    )
    public void getUserByEmail() {}

    @Operation(
        summary = "Update user",
        description = "Updates an existing user's data",
        parameters = @Parameter(
            name = "id", 
            in = ParameterIn.PATH, 
            description = "ID of the user to update", 
            required = true,
            schema = @Schema(type = "integer", format = "int64")
        ),
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "New user data",
            required = true,
            content = @Content(schema = @Schema(implementation = UpdateUserRequestDto.class))
        ),
        responses = {
            @ApiResponse(
                responseCode = "200", 
                description = "User updated successfully",
                content = @Content(schema = @Schema(implementation = UserResponseDto.class))
            ),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "409", description = "Email is already registered by another user")
        }
    )
    public void updateUser() {}

    @Operation(
        summary = "Delete user",
        description = "Deletes a user from the system",
        parameters = @Parameter(
            name = "id", 
            in = ParameterIn.PATH, 
            description = "ID of the user to delete", 
            required = true,
            schema = @Schema(type = "integer", format = "int64")
        ),
        responses = {
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
        }
    )
    public void deleteUser() {}

    @Operation(
        summary = "List all users",
        description = "Retrieves a list of all users in the system",
        responses = {
            @ApiResponse(
                responseCode = "200", 
                description = "List of users retrieved successfully",
                content = @Content(schema = @Schema(implementation = UserResponseDto[].class))
            )
        }
    )
    public void getAllUsers() {}
}