package com.example.data.model

enum class SessionRole {
    GUEST,
    ADMIN,
    PARENT
}

data class UserSession(
    val role: SessionRole = SessionRole.GUEST,
    val username: String = "",
    val displayName: String = "",
    val centerId: String = "AW-Center-042",
    val centerName: String = "AW-Center-042 (Shivaji Nagar)",
    val authenticatedChildId: String? = null
) {
    val isAuthenticated: Boolean
        get() = role != SessionRole.GUEST

    val isAdmin: Boolean
        get() = role == SessionRole.ADMIN

    val isParent: Boolean
        get() = role == SessionRole.PARENT
}
