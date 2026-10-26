package com.example.form_validation.validation

import androidx.annotation.StringRes
import com.example.form_validation.R
import com.example.form_validation.model.Gender
import com.example.form_validation.model.User
import java.util.Calendar

/**
 * Each `validate*` function returns `null` when the value is valid,
 * otherwise the string resource id of the error message.
 */
object UserValidator {

    const val NAME_MIN_LENGTH = 2
    const val NAME_MAX_LENGTH = 50
    const val PASSWORD_MIN_LENGTH = 8
    const val MIN_AGE = 13
    const val MAX_AGE = 120

    private val NAME_REGEX = Regex("^[\\p{L}\\p{M} ]+$")
    private val EMAIL_REGEX =
        Regex("^[A-Za-z0-9]+([._%+-][A-Za-z0-9]+)*@[A-Za-z0-9]+([.-][A-Za-z0-9]+)*\\.[A-Za-z]{2,}$")
    private val PHONE_REGEX = Regex("^0[35789]\\d{8}$")

    @StringRes
    fun validateFullName(input: String): Int? {
        val name = input.trim()
        return when {
            name.isEmpty() -> R.string.error_full_name_required
            name.length !in NAME_MIN_LENGTH..NAME_MAX_LENGTH -> R.string.error_full_name_length
            !NAME_REGEX.matches(name) -> R.string.error_full_name_invalid
            name.split(Regex("\\s+")).size < 2 -> R.string.error_full_name_words
            else -> null
        }
    }

    @StringRes
    fun validateEmail(input: String): Int? {
        val email = input.trim()
        return when {
            email.isEmpty() -> R.string.error_email_required
            !EMAIL_REGEX.matches(email) -> R.string.error_email_invalid
            else -> null
        }
    }

    @StringRes
    fun validatePhone(input: String): Int? {
        val phone = input.trim()
        return when {
            phone.isEmpty() -> R.string.error_phone_required
            !PHONE_REGEX.matches(phone) -> R.string.error_phone_invalid
            else -> null
        }
    }

    @StringRes
    fun validateBirthDate(birthDateMillis: Long?, today: Calendar = Calendar.getInstance()): Int? {
        if (birthDateMillis == null) return R.string.error_birth_date_required
        if (birthDateMillis > today.timeInMillis) return R.string.error_birth_date_future
        val age = User.calculateAge(birthDateMillis, today)
        return when {
            age < MIN_AGE -> R.string.error_birth_date_too_young
            age > MAX_AGE -> R.string.error_birth_date_too_old
            else -> null
        }
    }

    @StringRes
    fun validateGender(gender: Gender?): Int? =
        if (gender == null) R.string.error_gender_required else null

    @StringRes
    fun validatePassword(password: String): Int? = when {
        password.isEmpty() -> R.string.error_password_required
        password.any { it.isWhitespace() } -> R.string.error_password_space
        password.length < PASSWORD_MIN_LENGTH -> R.string.error_password_length
        password.none { it.isUpperCase() } -> R.string.error_password_upper
        password.none { it.isLowerCase() } -> R.string.error_password_lower
        password.none { it.isDigit() } -> R.string.error_password_digit
        password.none { it.isSpecialChar() } -> R.string.error_password_special
        else -> null
    }

    @StringRes
    fun validateConfirmPassword(password: String, confirm: String): Int? = when {
        confirm.isEmpty() -> R.string.error_confirm_password_required
        confirm != password -> R.string.error_confirm_password_mismatch
        else -> null
    }

    @StringRes
    fun validateTerms(accepted: Boolean): Int? =
        if (accepted) null else R.string.error_terms_required

    /** Returns a score from 0 (empty) to 4 (strong). */
    fun passwordStrength(password: String): Int {
        if (password.isEmpty()) return 0
        var score = 0
        if (password.length >= PASSWORD_MIN_LENGTH) score++
        if (password.any { it.isUpperCase() } && password.any { it.isLowerCase() }) score++
        if (password.any { it.isDigit() }) score++
        if (password.any { it.isSpecialChar() }) score++
        return score.coerceAtLeast(1)
    }

    private fun Char.isSpecialChar() = !isLetterOrDigit() && !isWhitespace()
}
