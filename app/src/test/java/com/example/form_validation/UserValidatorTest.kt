package com.example.form_validation

import com.example.form_validation.model.Gender
import com.example.form_validation.model.User
import com.example.form_validation.validation.UserValidator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.util.Calendar

class UserValidatorTest {

    private val today: Calendar = Calendar.getInstance().apply {
        set(2026, Calendar.OCTOBER, 26, 12, 0, 0)
        set(Calendar.MILLISECOND, 0)
    }

    private fun dateMillis(year: Int, month: Int, day: Int): Long =
        Calendar.getInstance().apply {
            set(year, month, day, 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

    @Test
    fun fullName_rules() {
        assertEquals(R.string.error_full_name_required, UserValidator.validateFullName("   "))
        assertEquals(R.string.error_full_name_length, UserValidator.validateFullName("A"))
        assertEquals(R.string.error_full_name_invalid, UserValidator.validateFullName("Nguyen Van 1"))
        assertEquals(R.string.error_full_name_words, UserValidator.validateFullName("Hung"))
        assertNull(UserValidator.validateFullName("Phạm Duy Hưng"))
    }

    @Test
    fun email_rules() {
        assertEquals(R.string.error_email_required, UserValidator.validateEmail(""))
        assertEquals(R.string.error_email_invalid, UserValidator.validateEmail("abc@"))
        assertEquals(R.string.error_email_invalid, UserValidator.validateEmail("abc..x@gmail.com"))
        assertEquals(R.string.error_email_invalid, UserValidator.validateEmail("abc@gmail"))
        assertNull(UserValidator.validateEmail("hung.pham@gmail.com"))
    }

    @Test
    fun phone_rules() {
        assertEquals(R.string.error_phone_required, UserValidator.validatePhone(""))
        assertEquals(R.string.error_phone_invalid, UserValidator.validatePhone("091234567"))
        assertEquals(R.string.error_phone_invalid, UserValidator.validatePhone("0123456789"))
        assertNull(UserValidator.validatePhone("0912345678"))
        assertNull(UserValidator.validatePhone("0387654321"))
    }

    @Test
    fun birthDate_rules() {
        assertEquals(R.string.error_birth_date_required, UserValidator.validateBirthDate(null, today))
        assertEquals(
            R.string.error_birth_date_future,
            UserValidator.validateBirthDate(dateMillis(2027, Calendar.JANUARY, 1), today)
        )
        assertEquals(
            R.string.error_birth_date_too_young,
            UserValidator.validateBirthDate(dateMillis(2013, Calendar.OCTOBER, 27), today)
        )
        assertNull(UserValidator.validateBirthDate(dateMillis(2013, Calendar.OCTOBER, 26), today))
        assertNull(UserValidator.validateBirthDate(dateMillis(2004, Calendar.MAY, 10), today))
    }

    @Test
    fun gender_rules() {
        assertEquals(R.string.error_gender_required, UserValidator.validateGender(null))
        assertNull(UserValidator.validateGender(Gender.FEMALE))
    }

    @Test
    fun password_rules() {
        assertEquals(R.string.error_password_required, UserValidator.validatePassword(""))
        assertEquals(R.string.error_password_space, UserValidator.validatePassword("Abc 123!"))
        assertEquals(R.string.error_password_length, UserValidator.validatePassword("Ab1!"))
        assertEquals(R.string.error_password_upper, UserValidator.validatePassword("abcdef1!"))
        assertEquals(R.string.error_password_lower, UserValidator.validatePassword("ABCDEF1!"))
        assertEquals(R.string.error_password_digit, UserValidator.validatePassword("Abcdefg!"))
        assertEquals(R.string.error_password_special, UserValidator.validatePassword("Abcdefg1"))
        assertNull(UserValidator.validatePassword("Abcdef1!"))
    }

    @Test
    fun confirmPassword_rules() {
        assertEquals(
            R.string.error_confirm_password_required,
            UserValidator.validateConfirmPassword("Abcdef1!", "")
        )
        assertEquals(
            R.string.error_confirm_password_mismatch,
            UserValidator.validateConfirmPassword("Abcdef1!", "Abcdef1?")
        )
        assertNull(UserValidator.validateConfirmPassword("Abcdef1!", "Abcdef1!"))
    }

    @Test
    fun terms_rules() {
        assertEquals(R.string.error_terms_required, UserValidator.validateTerms(false))
        assertNull(UserValidator.validateTerms(true))
    }

    @Test
    fun passwordStrength_scores() {
        assertEquals(0, UserValidator.passwordStrength(""))
        assertEquals(1, UserValidator.passwordStrength("abc"))
        assertEquals(2, UserValidator.passwordStrength("abcdefg1"))
        assertEquals(3, UserValidator.passwordStrength("Abcdefg1"))
        assertEquals(4, UserValidator.passwordStrength("Abcdefg1!"))
    }

    @Test
    fun user_age_and_initials() {
        val user = User(
            fullName = "Phạm Duy Hưng",
            email = "hung@gmail.com",
            phone = "0912345678",
            birthDateMillis = dateMillis(2004, Calendar.DECEMBER, 1),
            gender = Gender.MALE,
            password = "Abcdef1!"
        )
        assertEquals(21, user.ageOn(today))
        assertEquals("PH", user.initials)
    }
}
