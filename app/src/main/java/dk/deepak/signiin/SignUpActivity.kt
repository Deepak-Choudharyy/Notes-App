package dk.deepak.signiin

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.FacebookSdk
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import dk.deepak.signiin.databinding.ActivitySignUpBinding


class SignUpActivity : AppCompatActivity() {
    private lateinit var  binding: ActivitySignUpBinding
    // Declaring a lateinit variable for FirebaseAuth
    private lateinit var auth: FirebaseAuth
//    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var googleSignInClient:GoogleSignInClient

    private lateinit var callbackManger:CallbackManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()


        /* LOGIN WITH FACEBOOK */


        // Initialize the Facebook SDK. This is required before using any Facebook features.
        FacebookSdk.sdkInitialize(getApplicationContext());
       // Create a callback manager for handling Facebook login callbacks.
        callbackManger = CallbackManager.Factory.create()

        // Set permissions for Facebook login. This determines the data that the app can access from the user's Facebook account.
        // In this case, "email" and "public_profile" permissions are requested.
        arrayOf<String?>("email", "public_profile")
        // Register a callback for the Facebook login button.
        binding.facebook.registerCallback(
            callbackManger,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    // Handle successful Facebook login. Obtain the access token and pass it to handleFacebookAccessToken function.
                    Log.d(TAG, "facebook:onSuccess:$loginResult")
                    handleFacebookAccessToken(loginResult.accessToken)
                }

                override fun onCancel() {
                    // Handle cancel event when user cancels the Facebook login process.
                    Log.d(TAG, "facebook:onCancel")
                }

                override fun onError(error: FacebookException) {
                    // Handle error that occurs during Facebook login.
                    Log.d(TAG, "facebook:onError", error)
                }
            },
        )


//       initialize FireBase Auth
    auth = FirebaseAuth.getInstance()

    // Set click listener for the "Sign-In" button
    binding.signInButton.setOnClickListener {
        val intent = Intent(this,LoginActivity::class.java)
        startActivity(intent)
        finish()
    }


        /* LOGIN WITH EMAIL & PASSWORD  */


    // Set click listener for the "Register" button
    binding.registerButton.setOnClickListener {

        // Get text from editText fields entered by the user
        val email = binding.email.text.toString()
        val userName = binding.userName.text.toString()
        val password = binding.password.text.toString()
        val repeatPassword = binding.repeatPassword.text.toString()

//            check if any field is empty or blank
        if(email.isEmpty() || userName.isEmpty() || password.isEmpty() || repeatPassword.isEmpty()){
            Toast.makeText(this, "Please fill all the details", Toast.LENGTH_SHORT).show()
        }else if(password!=repeatPassword){
            // Check if password and repeat password fields match
            Toast.makeText(this, "Password and Repeat Password must be same", Toast.LENGTH_SHORT).show()
        }else{
            // Create a new user with email and password
            auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener { task ->
                    if(task.isSuccessful){
                        // If registration is successful, display a success message and redirect to LoginActivity
                        Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this,LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    }else{
                        // If registration fails, display an error message
                        Toast.makeText(this, "Registration failed:${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }


        /* LOGIN WITH GOOGLE */


      // Build GoogleSignInOptions with the default sign-in method, requesting ID token and email
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            // Request ID token using the default web client ID
            .requestIdToken(getString(R.string.default_web_client_id))
            // Request user's email address
            .requestEmail()
            .build()

        // Initialize Google Sign-In client
        googleSignInClient = GoogleSignIn.getClient(this,gso)

        // Set click listener for Google Sign-In button
        binding.google.setOnClickListener {
            // Get the sign-in intent from the Google Sign-In client
            val signInClient = googleSignInClient.signInIntent
            // Launch the activity for result using the activity result launcher
            launcher.launch(signInClient)
        }

    }

    // Activity result launcher for handling the result of Google Sign-In activity
    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        // Check if the result code indicates success
        if(result.resultCode==Activity.RESULT_OK){
            // Extract the GoogleSignInAccount from the result data
            val task=GoogleSignIn.getSignedInAccountFromIntent(result.data)
            // Check if the sign-in task was successful
            if(task.isSuccessful){
                // Retrieve the GoogleSignInAccount from the task result
                val account:GoogleSignInAccount?=task.result
                // Create a credential from the GoogleSignInAccount's ID token
                val credential=GoogleAuthProvider.getCredential(account?.idToken,null)
                // Sign in to Firebase Authentication using the credential
                auth.signInWithCredential(credential).addOnCompleteListener{
                    // Check if the sign-in process to Firebase is successful
                    if (it.isSuccessful){
                        // If sign-in is successful, navigate to MainActivity
                        val intent = Intent(this,MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }else {
                        // If sign-in fails, display an error message
                        Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }

        }else{
            // If sign-in fails, display an error message
            Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
        }
    }




    // Handle the result of an activity started for result. Pass the activity result back to the Facebook SDK.
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Pass the activity result back to the Facebook SDK
        callbackManger.onActivityResult(requestCode,resultCode,data)
    }

    // Handle the Facebook access token to authenticate with Firebase.
    private fun handleFacebookAccessToken(token: AccessToken) {
        Log.d(TAG, "handleFacebookAccessToken:$token")
        // Obtain a Firebase credential from the Facebook access token.
        val credential = FacebookAuthProvider.getCredential(token.token)

        // Sign in to Firebase with the Facebook credential.
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // If sign-in is successful, display a success message, navigate to MainActivity, and finish the current activity.
                    Toast.makeText(this, "Authentication Success.", Toast.LENGTH_SHORT,).show()
                    // Sign in success, update UI with the signed-in user's information
                    val intent = Intent(this,MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT,).show()
                }
            }
    }


}