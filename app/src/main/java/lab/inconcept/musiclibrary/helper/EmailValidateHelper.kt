package lab.inconcept.musiclibrary.helper

import java.util.regex.Pattern

object EmailValidateHelper {
    fun isValid(email: String?): Boolean {
        val emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$"
        val pattern = Pattern.compile(emailRegex)
        return if (email == null) {
            false
        } else pattern.matcher(email).matches()
    }
}