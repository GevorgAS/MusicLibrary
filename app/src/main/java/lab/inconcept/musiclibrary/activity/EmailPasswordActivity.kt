package lab.inconcept.musiclibrary.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import lab.inconcept.musiclibrary.R
import lab.inconcept.musiclibrary.helper.Constants
import lab.inconcept.musiclibrary.helper.EmailValidateHelper
import lab.inconcept.musiclibrary.helper.PreferenceManager
import lab.inconcept.musiclibrary.helper.Utils

class EmailPasswordActivity : AppCompatActivity(), View.OnClickListener {
    private var mEmailEditText: AppCompatEditText? = null
    private var mPasswordEditText: AppCompatEditText? = null
    private var mSignInButton: AppCompatButton? = null
    private var mProgressBar: ProgressBar? = null
    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email_password)
        init()
    }

    private fun init() {
        mEmailEditText = findViewById(R.id.email)
        mPasswordEditText = findViewById(R.id.password)
        mSignInButton = findViewById(R.id.sign_in)
        mProgressBar = findViewById(R.id.progressBar)
        mAuth = FirebaseAuth.getInstance()
        mSignInButton!!.setOnClickListener(this)
    }

    private fun showProgressBar(show: Boolean) {
        mProgressBar!!.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun onStart() {
        super.onStart()
        val currentUser = mAuth!!.currentUser
        updateUI(currentUser)
    }

    override fun onBackPressed() {
        quit()
    }

    private fun quit() {
        val quit = Intent(Intent.ACTION_MAIN)
        quit.addCategory(Intent.CATEGORY_HOME)
        quit.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        quit.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(quit)
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user == null) {
            showProgressBar(false)
            mSignInButton!!.isFocusable = true
            mSignInButton!!.isClickable = true
            return
        }
        showProgressBar(false)
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun createAccount() {
        val email = PreferenceManager.getString(this, Constants.userEmail, "")
        val password = PreferenceManager.getString(this, Constants.userPassword, "")
        if (email == null || password == null) {
            return
        }
        mAuth!!.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        signIn(email, password)
                    } else {
                        if (task.exception is FirebaseAuthUserCollisionException) {
                            signIn(email, password)
                            return@addOnCompleteListener
                        }
                        Toast.makeText(this@EmailPasswordActivity, getString(R.string.error_authentication),
                                Toast.LENGTH_SHORT).show()
                        showProgressBar(false)
                        updateUI(null)
                    }
                }
    }

    private fun signIn(email: String, password: String) {
        mAuth!!.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = mAuth!!.currentUser
                        updateUI(user)
                    } else {
                        Toast.makeText(this@EmailPasswordActivity, task.exception!!.message,
                                Toast.LENGTH_SHORT).show()
                        showProgressBar(false)
                        updateUI(null)
                    }
                }
    }

    override fun onClick(v: View) {
        val viewId = v.id
        when (viewId) {
            R.id.sign_in -> {
                Utils.hideKeyboard(v)
                showProgressBar(true)
                mSignInButton!!.isClickable = false
                mSignInButton!!.isFocusable = false
                if (handleInputs()) {
                    createAccount()
                }
            }
        }

    }

    private fun handleInputs(): Boolean {
        val emailEditable = mEmailEditText!!.text
        val passwordEditable = mPasswordEditText!!.text
        if (emailEditable != null && passwordEditable != null) {
            val email = emailEditable.toString()
            val password = passwordEditable.toString()
            if (!EmailValidateHelper.isValid(email)) {
                showProgressBar(false)
                mEmailEditText!!.error = getString(R.string.invalid_email)
                mSignInButton!!.isFocusable = true
                mSignInButton!!.isClickable = true
                return false
            }
            if (password == "") {
                mEmailEditText!!.error = getString(R.string.invalid_email)
                showProgressBar(false)
                mSignInButton!!.isFocusable = true
                mSignInButton!!.isClickable = true
                return false
            }
            PreferenceManager.putString(this, Constants.userEmail, email)
            PreferenceManager.putString(this, Constants.userPassword, password)
        } else {
            Toast.makeText(this, getString(R.string.error_email_and_password), Toast.LENGTH_SHORT).show()
            showProgressBar(false)
            mSignInButton!!.isFocusable = true
            mSignInButton!!.isClickable = true
            return false
        }
        return true
    }
}
