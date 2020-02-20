package lab.inconcept.musiclibrary.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

import lab.inconcept.musiclibrary.R;
import lab.inconcept.musiclibrary.helper.Constants;
import lab.inconcept.musiclibrary.helper.EmailValidateHelper;
import lab.inconcept.musiclibrary.helper.PreferenceManager;
import lab.inconcept.musiclibrary.helper.Utils;

public class EmailPasswordActivity extends AppCompatActivity implements View.OnClickListener {
    private AppCompatEditText mEmailEditText;
    private AppCompatEditText mPasswordEditText;
    private AppCompatButton mSignInButton;
    private ProgressBar mProgressBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_password);
        init();
    }

    private void init() {
        mEmailEditText = findViewById(R.id.email);
        mPasswordEditText = findViewById(R.id.password);
        mSignInButton = findViewById(R.id.sign_in);
        mProgressBar = findViewById(R.id.progressBar);
        mAuth = FirebaseAuth.getInstance();
        mSignInButton.setOnClickListener(this);
    }

    private void showProgressBar(boolean show) {
        mProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        final FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    @Override
    public void onBackPressed() {
        quit();
    }

    private void quit() {
        final Intent quit = new Intent(Intent.ACTION_MAIN);
        quit.addCategory(Intent.CATEGORY_HOME);
        quit.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        quit.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(quit);
    }

    private void updateUI(final FirebaseUser user) {
        if (user == null) {
            showProgressBar(false);
            mSignInButton.setFocusable(true);
            mSignInButton.setClickable(true);
            return;
        }
        showProgressBar(false);
        final Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void createAccount() {
        final String email = PreferenceManager.getString(this, Constants.userEmail, "");
        final String password = PreferenceManager.getString(this, Constants.userPassword, "");
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        signIn(email, password);
                    } else {
                        if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                            signIn(email, password);
                            return;
                        }
                        Toast.makeText(EmailPasswordActivity.this, getString(R.string.error_authentication),
                                Toast.LENGTH_SHORT).show();
                        showProgressBar(false);
                        updateUI(null);
                    }
                });
    }

    private void signIn(final String email, final String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        final FirebaseUser user = mAuth.getCurrentUser();
                        updateUI(user);
                    } else {
                        Toast.makeText(EmailPasswordActivity.this, task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                        showProgressBar(false);
                        updateUI(null);
                    }
                });
    }

    @Override
    public void onClick(View v) {
        final int viewId = v.getId();
        switch (viewId) {
            case R.id.sign_in:
                Utils.hideKeyboard(v);
                showProgressBar(true);
                mSignInButton.setClickable(false);
                mSignInButton.setFocusable(false);
                if (handleInputs()) {
                    createAccount();
                }
                break;
        }

    }

    private boolean handleInputs() {
        final Editable emailEditable = mEmailEditText.getText();
        final Editable passwordEditable = mPasswordEditText.getText();
        if (emailEditable != null && passwordEditable != null) {
            String email = emailEditable.toString();
            String password = passwordEditable.toString();
            if (!EmailValidateHelper.isValid(email)) {
                showProgressBar(false);
                mEmailEditText.setError(getString(R.string.invalid_email));
                mSignInButton.setFocusable(true);
                mSignInButton.setClickable(true);
                return false;
            }
            if (password.equals("")) {
                mEmailEditText.setError(getString(R.string.invalid_email));
                showProgressBar(false);
                mSignInButton.setFocusable(true);
                mSignInButton.setClickable(true);
                return false;
            }
            PreferenceManager.putString(this, Constants.userEmail, email);
            PreferenceManager.putString(this, Constants.userPassword, password);
        } else {
            Toast.makeText(this, getString(R.string.error_email_and_password), Toast.LENGTH_SHORT).show();
            showProgressBar(false);
            mSignInButton.setFocusable(true);
            mSignInButton.setClickable(true);
            return false;
        }
        return true;
    }
}
