package pranav.utils

@Suppress("MemberVisibilityCanBePrivate", "unused")
object RegexPatterns {
	const val EMAIL_PATTERN = "^(([^<>()\\[\\]\\\\.,;:\\s@\"]+(\\.[^<>()\\[\\]\\\\.,;:\\s@\"]+)*)|(\".+\"))@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$"

	const val PASSWORD_PATTERN =
			"^[a-zA-Z0-9\\s]{6,}$"

	fun isEmailValid(email: String): Boolean =
			email.matches(EMAIL_PATTERN.toRegex())

	fun isPasswordValid(password: String): Boolean =
			password.matches(PASSWORD_PATTERN.toRegex())
}
