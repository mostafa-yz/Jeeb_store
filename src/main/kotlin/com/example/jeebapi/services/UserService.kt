package com.example.jeebapi.services

import com.example.jeebapi.DTO.Userdto
import com.example.jeebapi.auth.CustomUserDetails
import com.example.jeebapi.models.User
import com.example.jeebapi.repository.UserRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import javax.accessibility.AccessibleRole.LIST

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {

    /**
     * Creates a new user. Only an admin can perform this action.
     * The new user's password will be hashed before saving.
     *
     * @param user The user object with plaintext password.
     * @return The saved user with a hashed password.
     * @throws IllegalAccessException if the current user is not an admin.
     * @throws IllegalArgumentException if the email already exists.
     */
    fun create(user: User): User {
        ensureCurrentUserIsAdmin()

        if (userRepository.existsByEmail(user.email)) {
            throw IllegalArgumentException("Email '${user.email}' already exists.")
        }

        // Ensure the password is not blank and hash it
        val hashedPassword = passwordEncoder.encode(user.password)
        val newUser = user.copy(password = hashedPassword)

        return userRepository.save(newUser)
    }

    /**
     * Updates an existing user's information. Only an admin can perform this action.
     * Note: This method does not update the password.
     *
     * @param id The ID of the user to update.
     * @param userDetails A user object containing the new details.
     * @return The updated user.
     * @throws IllegalAccessException if the current user is not an admin.
     * @throws EntityNotFoundException if the user does not exist.
     */
    fun update(id: Long, userDetails: User): User {
        ensureCurrentUserIsAdmin()

        val existingUser = userRepository.findById(id)
            .orElseThrow { EntityNotFoundException("User with ID $id not found.") }

        val updatedUser = existingUser.copy(
            name = userDetails.name,
            email = userDetails.email,
            accesslevel = userDetails.accesslevel
            // Password is intentionally not updated here
        )
        return userRepository.save(updatedUser)
    }

    /**
     * Deletes a user by their ID. Only an admin can perform this action,
     * and an admin user cannot be deleted.
     *
     * @param id The ID of the user to delete.
     * @throws IllegalAccessException if the current user is not an admin or is trying to delete an admin.
     * @throws EntityNotFoundException if the user does not exist.
     */
    fun deleteById(id: Long) {
        ensureCurrentUserIsAdmin()

        val userToDelete = findById(id) // findById already throws EntityNotFoundException

        // Business Rule: An admin account cannot be deleted.
        if (userToDelete.accesslevel == User.LEVEL_ADMIN) {
            throw IllegalAccessException("Deletion failed: Admin users cannot be deleted.")
        }

        userRepository.deleteById(id)
    }

    /**
     * Retrieves all users. This action is restricted to admins.
     *
     * @return A list of all users.
     * @throws IllegalAccessException if the current user is not an admin.
     */
    fun findAll(): List<Userdto> {
        ensureCurrentUserIsAdmin()
        return userRepository.findAll().map { user ->
            Userdto(
                id = user.id,
                name = user.name,
                email = user.email,
                accesslevel = user.accesslevel,

            )
        }
    }

    /**
     * Finds a single user by their ID.
     *
     * @param id The ID of the user to find.
     * @return The found user.
     * @throws EntityNotFoundException if no user is found.
     */
    fun findById(id: Long): User {
        return userRepository.findById(id)
            .orElseThrow { EntityNotFoundException("User with ID $id not found.") }
    }


    /**
     * A private helper function to check if the currently authenticated user has admin privileges.
     * It relies on a CustomUserDetails object being present in the security context.
     *
     * @throws IllegalAccessException if the user is not authenticated or not an admin.
     */
    private fun ensureCurrentUserIsAdmin() {
        val principal = SecurityContextHolder.getContext().authentication.principal

        val hasAdminAccess = if (principal is CustomUserDetails) {
            principal.accesslevel >= User.LEVEL_ADMIN
        } else {
            // This case might occur for unauthenticated users or if security is misconfigured
            false
        }

        if (!hasAdminAccess) {
            throw IllegalAccessException("Access Denied: This action requires admin privileges.")
        }
    }
}
