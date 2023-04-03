package com.example.chatapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.GoogleAuthProvider


class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Initialize Firebase Auth
        auth = Firebase.auth
        val login = findViewById<Button>(R.id.lgbtn)

        login.setOnClickListener {

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        val register = findViewById<Button>(R.id.signupbtn)

        register.setOnClickListener {

            val email = findViewById<EditText>(R.id.email)
            val pass = findViewById<EditText>(R.id.password)
            val phone = findViewById<EditText>(R.id.phone)
            val name = findViewById<EditText>(R.id.name)

            val email1 = email.text.toString()
            val password = pass.text.toString()
            val phone1 = phone.text.toString()
            val name1 = name.text.toString()

            if (email.text.isEmpty() || pass.text.isEmpty() || phone.text.isEmpty())       {
                Toast.makeText(baseContext, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener

            }

            auth.createUserWithEmailAndPassword(email1, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        //add all users to activity
                        addUserToDatabase(name1,email1,auth.currentUser?.uid!!)
                        // Sign in success, update UI with the signed-in user's information
                        Toast.makeText(baseContext, "success",
                            Toast.LENGTH_SHORT).show()

                        //                val database = Firebase.database.reference
                        //               val user = User(name1)
                        //                database.child("users").child(usernamee).setValue(user)


                        auth = Firebase.auth

                        val user = auth.currentUser
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName(name1) // Set the name
                            .build()
                        user?.updateProfile(profileUpdates)
                            ?.addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    // Name updated successfully
                                    val intent = Intent(this, MainActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                } else {
                                    // Name update failed
                                }
                            }

                        val userUpdates = hashMapOf<String, Any>(
                            "phone" to phone1 // Set the phone number
                        )
                        val db = FirebaseFirestore.getInstance()
                        db.collection("users").document(user?.uid.toString())
                            .set(userUpdates)
                            .addOnSuccessListener { documentReference ->
                                // Phone number added successfully
                            }
                            .addOnFailureListener { e ->
                                // Phone number addition failed
                            }

                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener{
                    Toast.makeText(this, "error occurred ${it.localizedMessage}", Toast.LENGTH_SHORT)
                        .show()
                }
        }

//      For google authentication
//        val gAuth = findViewById<TextView>(R.id.info)
//        val gOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestEmail()
//            .build()
//        val gClient = GoogleSignIn.getClient(this, gOptions)
//        val gAccount = GoogleSignIn.getLastSignedInAccount(this)
//        if (gAccount != null) {
//            finish()
//            val intent = Intent(this@RegisterActivity, MainActivity::class.java)
//            startActivity(intent)
//        }
//
//        val activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//            if (result.resultCode == Activity.RESULT_OK) {
//                val data = result.data
//                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
//
//                try {
//                    task.getResult(ApiException::class.java)
//                    finish()
//                    val intent = Intent(this@RegisterActivity, MainActivity::class.java)
//                    startActivity(intent)
//                } catch (e: ApiException) {
//                    Toast.makeText(this@RegisterActivity, "Something went wrong", Toast.LENGTH_SHORT).show()
//                }
//                handleResults(task)
//            }
//        }
//
//        gAuth.setOnClickListener {
//            val signInIntent = gClient.signInIntent
//            activityResultLauncher.launch(signInIntent)
//        }

    }

    private fun addUserToDatabase(name: String, email: String, uid: String){
        mDbRef = FirebaseDatabase.getInstance().getReference()
        mDbRef.child("user").child(uid).setValue(User(name,email,uid))
    }

//    private fun handleResults(task: Task<GoogleSignInAccount>) {
//        if (task.isSuccessful){
//            val account : GoogleSignInAccount? = task.result
//            if (account != null){
//                updateUI(account)
//            }
//        }else{
//            Toast.makeText(this, task.exception.toString() , Toast.LENGTH_SHORT).show()
//        }
//    }

//    private fun updateUI(account: GoogleSignInAccount) {
//        val credential = GoogleAuthProvider.getCredential(account.idToken , null)
//        auth.signInWithCredential(credential).addOnCompleteListener {
//            if (it.isSuccessful){
//                val intent : Intent = Intent(this , MainActivity::class.java)
////                intent.putExtra("email" , account.email)
////                intent.putExtra("name" , account.displayName)
//                account.displayName?.let { it1 -> account.email?.let { it2 ->
//                    account.id?.let { it3 ->
//                        addUserToDatabase(it1,
//                            it2, it3
//                        )
//                    }
//                    Toast.makeText(baseContext, "success",
//                        Toast.LENGTH_SHORT).show()
//                } }
//                startActivity(intent)
//                finish()
//            }else{
//                Toast.makeText(this, it.exception.toString() , Toast.LENGTH_SHORT).show()
//
//            }
//        }
//    }
}
