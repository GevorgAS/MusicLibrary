package lab.inconcept.musiclibrary.activity.email_password

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import lab.inconcept.musiclibrary.databinding.ActivityEmailPasswordBinding

class EmailPasswordActivity : AppCompatActivity() {

    lateinit var emailPasswordViewModel: EmailPasswordViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        emailPasswordViewModel = ViewModelProvider(this)[EmailPasswordViewModel::class.java]
        val binding = ActivityEmailPasswordBinding.inflate(layoutInflater).apply {
            viewModel = emailPasswordViewModel
            lifecycleOwner = this@EmailPasswordActivity
        }
        setContentView(binding.root)
        emailPasswordViewModel.onCreate(this)
    }

    override fun onStart() {
        super.onStart()
        emailPasswordViewModel.onStart()
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
}
