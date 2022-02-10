package com.uzlov.dating.lavada.ui.fragments.registration

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FacebookAuthProvider
import com.uzlov.dating.lavada.R
import com.uzlov.dating.lavada.app.appComponent
import com.uzlov.dating.lavada.auth.FirebaseEmailAuthService
import com.uzlov.dating.lavada.auth.FirebaseEmailAuthService.Companion.TAG
import com.uzlov.dating.lavada.auth.User
import com.uzlov.dating.lavada.databinding.FragmentRegistrationBinding
import com.uzlov.dating.lavada.ui.fragments.BaseFragment
import com.uzlov.dating.lavada.ui.fragments.profile.AboutMyselfFragment
import org.json.JSONException
import org.json.JSONObject
import java.util.regex.Pattern
import javax.inject.Inject


class RegistrationFragment :
    BaseFragment<FragmentRegistrationBinding>(FragmentRegistrationBinding::inflate) {

    private lateinit var callbackManager: CallbackManager

    lateinit var email: String
    lateinit var firstName: String

    private lateinit var googleSignInClient: GoogleSignInClient

    @Inject
    lateinit var firebaseEmailAuthService: FirebaseEmailAuthService

    lateinit var user: User

    private val mainActivityResultLauncher =
        registerForActivityResult(StartActivityForResult()) { result ->

            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                try {
                    val account = task.getResult(ApiException::class.java)!!
                    Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                    firebaseEmailAuthService.firebaseAuthWithGoogle(account.idToken!!)
                    updateUI()
                } catch (e: ApiException) {
                    Log.w(TAG, "Google sign in failed", e)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        callbackManager = CallbackManager.Factory.create()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Pass the activity result back to the Facebook SDK
        callbackManager.onActivityResult(requestCode, resultCode, data)
        Log.d("letsSee", "zzzzzzzzz: $data")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireContext().appComponent.inject(this)
        checkUser()
        addTextChangedListener()
        googleSignInClient =
            GoogleSignIn.getClient(requireContext(), firebaseEmailAuthService.getGSO(context!!))

        viewBinding.btnLogin.setOnClickListener {
            val email = viewBinding.tiEtEmail.text.toString()
            val password = viewBinding.textInputPassword.text.toString()
            firebaseEmailAuthService.registered(email, password)
            updateUI()
        }
        viewBinding.btnLoginWithGoogle.setOnClickListener {
            onAct()
        }

        viewBinding.btnLoginWithFacebook.setReadPermissions("email")
        viewBinding.btnLoginWithFacebook.fragment = this
        viewBinding.btnLoginWithFacebook.registerCallback(callbackManager, object :
            FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                Log.d(TAG, "facebook:onSuccess:$loginResult")
                handleFacebookAccessToken(loginResult.accessToken)
            }

            override fun onCancel() {
                Log.d(TAG, "facebook:onCancel")
            }

            override fun onError(error: FacebookException) {
                Log.d(TAG, "facebook:onError", error)
            }
        })
        arghhhh()
    }
fun arghhhh(){
    LoginManager.getInstance().logInWithReadPermissions(this, listOf("email", "public_profile"))

    LoginManager.getInstance()
        .registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {

                val token = loginResult.accessToken

                val request = GraphRequest.newMeRequest(
                    token,
                    object : GraphRequest.GraphJSONObjectCallback {
                        override fun onCompleted(
                            obj: JSONObject?,
                            response: GraphResponse?
                        ) {

                            try {
                                // Save user email to variable
                                email = obj!!.getString("email")
                                firstName = obj.getString("first_name")
                                Log.d(TAG, "1) Facebook email received $email and name $firstName")
                                handleFacebookAccessToken(token)
                            }
                            catch (e: JSONException) {
                                Toast.makeText(
                                    context,
                                    "Facebook Authentication Failed.",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    })

                val parameters = Bundle()
                parameters.putString("fields", "email,first_name,last_name")
                request.parameters = parameters
                request.executeAsync()
            }

            override fun onCancel() {
                Log.d(TAG, "facebook:onCancel")
                Toast.makeText(context, "Facebook Login Cancelled", Toast.LENGTH_LONG).show()
            }

            override fun onError(error: FacebookException) {
                Log.d(TAG, "facebook:onError", error)
                Toast.makeText(context, "Facebook Authentication Failed", Toast.LENGTH_LONG)
                    .show()
            }
        })
}
    private fun handleFacebookAccessToken(token: AccessToken) {
        Log.d(TAG, "handleFacebookAccessToken:$token")

        val credential = FacebookAuthProvider.getCredential(token.token)
        firebaseEmailAuthService.auth.signInWithCredential(credential)
            .addOnCompleteListener(Activity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    updateUI()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(
                        context, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun checkUser() {
        val currentUser = firebaseEmailAuthService.auth.currentUser
        if (currentUser != null) {
            updateUI()
        }
    }

    private fun onAct() {
        val intent = googleSignInClient.signInIntent
        mainActivityResultLauncher.launch(intent)
    }

    private fun updateUI() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.container, AboutMyselfFragment.newInstance())
            .addToBackStack(null)
            .commit()
    }

    private fun isValidEmail(email: String): Boolean {
        val pattern = Patterns.EMAIL_ADDRESS
        return pattern.matcher(email).matches()
    }

    private fun isValidPassword(password: String): Boolean {
        val passwordREGEX = Pattern.compile(
            "^" +
                    "(?=.*[0-9])" +
                    "(?=.*[a-z])" +
                    "(?=.*[A-Z])" +
                    "(?=.*[a-zA-Z])" +
                    "(?=\\S+$)" +
                    ".{8,}" +
                    "$"
        )
        return passwordREGEX.matcher(password).matches()
    }

    private fun addTextChangedListener() {
        viewBinding.tiEtEmail.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                verifyEditText()
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })
        viewBinding.textInputPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                verifyEditText()
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })
    }

    private fun verifyEditText() {
        with(viewBinding) {
            btnLogin.isEnabled = isValidEmail(viewBinding.tiEtEmail.text.toString()) &&
                    isValidPassword(viewBinding.textInputPassword.text.toString())
        }
    }
}
