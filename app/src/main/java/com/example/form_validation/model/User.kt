package com.example.form_validation.model

import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

enum class Gender(val label: String) {
    MALE("Nam"),
    FEMALE("Nữ"),
    OTHER("Khác")
}

data class User(
    val fullName: String,
    val email: String,
    val phone: String,
    val birthDateMillis: Long,
    val gender: Gender,
    val password: String
) : Serializable {

    val formattedBirthDate: String
        get() = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(birthDateMillis)

    val initials: String
        get() = fullName.trim()
            .split(Regex("\\s+"))
            .filter { it.isNotEmpty() }
            .let { words -> listOfNotNull(words.firstOrNull(), words.drop(1).lastOrNull()) }
            .joinToString("") { it.first().uppercase() }

    fun ageOn(today: Calendar = Calendar.getInstance()): Int = calculateAge(birthDateMillis, today)

    companion object {
        const val EXTRA_USER = "extra_user"

        fun calculateAge(birthDateMillis: Long, today: Calendar = Calendar.getInstance()): Int {
            val birth = Calendar.getInstance().apply { timeInMillis = birthDateMillis }
            var age = today.get(Calendar.YEAR) - birth.get(Calendar.YEAR)
            val birthdayNotReached = today.get(Calendar.MONTH) < birth.get(Calendar.MONTH) ||
                (today.get(Calendar.MONTH) == birth.get(Calendar.MONTH) &&
                    today.get(Calendar.DAY_OF_MONTH) < birth.get(Calendar.DAY_OF_MONTH))
            if (birthdayNotReached) age--
            return age
        }
    }
}
