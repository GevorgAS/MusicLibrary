package lab.inconcept.musiclibrary.activity.email_password

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.databinding.BindingAdapter
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import lab.inconcept.musiclibrary.R
import lab.inconcept.musiclibrary.activity.main.MainActivity
import lab.inconcept.musiclibrary.helper.Constants
import lab.inconcept.musiclibrary.helper.EmailValidateHelper
import lab.inconcept.musiclibrary.helper.PreferenceManager
import lab.inconcept.musiclibrary.helper.Utils
import java.lang.ref.WeakReference

class EmailPasswordViewModel : ViewModel() {
    private lateinit var auth: FirebaseAuth
    private lateinit var weak: WeakReference<EmailPasswordActivity>

    val loading = MutableLiveData<Boolean>()
    val enableButton = MutableLiveData<Boolean>()
    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val emailError = MutableLiveData<String>()
    val passwordError = MutableLiveData<String>()


    fun onCreate(activity: EmailPasswordActivity) {
        weak = WeakReference(activity)
        auth = FirebaseAuth.getInstance()
    }

    fun onStart() {
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun showProgressBar(show: Boolean) {
        loading.postValue(show)
    }

    private fun enableButton(show: Boolean) {
        enableButton.postValue(show)
    }

    private fun setEmailError(error: String?) {
        emailError.postValue(error)
    }

    private fun setPasswordError(error: String?) {
        emailError.postValue(error)
    }


    private fun updateUI(user: FirebaseUser?) {
        val context = weak.get() ?: throw IllegalStateException("Context is null")
        if (user == null) {
            showProgressBar(false)
            enableButton(true)
            return
        }
        showProgressBar(false)
        val intent = Intent(context, MainActivity::class.java)
        context.startActivity(intent)
        context.finish()
    }

    private fun createAccount() {
        val context = weak.get() ?: throw IllegalStateException("Context is null")
        val email = PreferenceManager.getString(context, Constants.userEmail, "")
        val password = PreferenceManager.getString(context, Constants.userPassword, "")
        if (email == null || password == null) {
            return
        }
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(context) { task ->
                    if (task.isSuccessful) {
                        signIn(context, email, password)
                    } else {
                        if (task.exception is FirebaseAuthUserCollisionException) {
                            signIn(context, email, password)
                            return@addOnCompleteListener
                        }
                        Toast.makeText(context, context.getString(R.string.error_authentication),
                                Toast.LENGTH_SHORT).show()
                        showProgressBar(false)
                        updateUI(null)
                    }
                }
    }

    private fun signIn(context: Context, email: String, password: String) {
        context as AppCompatActivity
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(context) { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        updateUI(user)
                    } else {
                        Toast.makeText(context, task.exception!!.message,
                                Toast.LENGTH_SHORT).show()
                        showProgressBar(false)
                        updateUI(null)
                    }
                }
    }

    fun onSignInClick(view: View) {
        Utils.hideKeyboard(view)
        showProgressBar(true)
        enableButton(false)
        if (handleInputs()) {
            createAccount()
        }
    }

    private fun handleInputs(): Boolean {
        val context = weak.get() ?: throw IllegalStateException("Context is null")
        val emailEditable = email.value
        val passwordEditable = password.value
        if (emailEditable != null && passwordEditable != null) {
            val email = emailEditable.toString()
            val password = passwordEditable.toString()
            if (!EmailValidateHelper.isValid(email)) {
                showProgressBar(false)
                setEmailError(context.getString(R.string.invalid_email))
                enableButton(true)
                return false
            }
            if (password == "") {
                setPasswordError(context.getString(R.string.invalid_password))
                showProgressBar(false)
                enableButton(true)
                return false
            }
            PreferenceManager.putString(context, Constants.userEmail, email)
            PreferenceManager.putString(context, Constants.userPassword, password)
        } else {
            Toast.makeText(context, context.getString(R.string.error_email_and_password), Toast.LENGTH_SHORT).show()
            showProgressBar(false)
            enableButton(true)
            return false
        }
        return true
    }
}

@BindingAdapter("enableClick")
fun enableClick(button: AppCompatButton, enable: Boolean) {
    button.apply {
        isClickable = enable
        isFocusable = enable
    }
}

@BindingAdapter("editError")
fun editError(editText: AppCompatEditText, error: String?) {
    editText.error = error
}

@BindingAdapter("visibleIf")
fun visibleIf(view: View, visible: Boolean) {
    view.visibility = if (visible) View.VISIBLE else View.GONE
}