package com.example.chatapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class RegisterActivity : Activity() {

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
    }

    private fun addUserToDatabase(name: String, email: String, uid: String){
        mDbRef = FirebaseDatabase.getInstance().getReference()
        mDbRef.child("user").child(uid).setValue(User(name,email,uid))
    }
}
