package com.example.proyectogym

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.proyectogym.databinding.ActivityCheckEmailBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase

class CheckEmailActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityCheckEmailBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCheckEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Initialize Firebase Auth
        auth = Firebase.auth

        val user = auth.currentUser

        binding.veficateEmailAppCompatButton.setOnClickListener {
            val profileUpdates = userProfileChangeRequest {}

            user!!.updateProfile(profileUpdates).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (user.isEmailVerified) {
                        reload()
                    } else {
                        Toast.makeText(this, "Porfavor Verificar Email", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }
        binding.signOutImageView.setOnClickListener {
            signOut()
        }
    }



    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if(currentUser != null){
            if(currentUser.isEmailVerified){
                reload()
            } else {
                sendEmailVerification()

            }
        }
    }

    private fun sendEmailVerification() {
        val user = auth.currentUser
        user!!.sendEmailVerification().addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Se Envio el correo de verificacion",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun reload() {
        val intent = Intent ( this, MainActivity::class.java)
        this.startActivity(intent)
    }
    private  fun signOut(){

        Firebase.auth.signOut()
        val intent = Intent(this,SignInActivity::class.java)
        startActivity(intent)

    }
}