package com.example.proyectogym

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import com.bumptech.glide.Glide
import com.example.proyectogym.databinding.ActivityEdit3Binding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class EditActivity3 : AppCompatActivity() {
    private lateinit var bindingActivityEdit: ActivityEdit3Binding
    private val file = 1
    private var fileUri: Uri? = null
    private var imageUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingActivityEdit = ActivityEdit3Binding.inflate(layoutInflater)
        val view = bindingActivityEdit.root
        setContentView(view)

        val key = intent.getStringExtra("key")
        val database = Firebase.database
        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS") val myRef = database.getReference("game").child(
            key.toString()
        )
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val mControllerProductos: ControllerProductos? = dataSnapshot.getValue(ControllerProductos::class.java)
                if (mControllerProductos != null) {

                    bindingActivityEdit.nameEditText.text = Editable.Factory.getInstance().newEditable(mControllerProductos.name)
                    bindingActivityEdit.dateEditText.text = Editable.Factory.getInstance().newEditable(mControllerProductos.date)
                    bindingActivityEdit.priceEditText.text = Editable.Factory.getInstance().newEditable(mControllerProductos.price)
                    bindingActivityEdit.descriptionEditText.text = Editable.Factory.getInstance().newEditable(mControllerProductos.description)

                    imageUrl = mControllerProductos.url.toString()

                    if(fileUri==null){
                        Glide.with(view)
                            .load(imageUrl)
                            .into(bindingActivityEdit.posterImageView)
                    }

                }

            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("TAG", "Failed to read value.", error.toException())
            }
        })

        bindingActivityEdit.saveButton.setOnClickListener {

            val name : String = bindingActivityEdit.nameEditText.text.toString()
            val date : String = bindingActivityEdit.dateEditText.text.toString()
            val price : String = bindingActivityEdit.priceEditText.text.toString()
            val description: String = bindingActivityEdit.descriptionEditText.text.toString()

            val folder: StorageReference = FirebaseStorage.getInstance().reference.child("game")
            val videoGameReference : StorageReference = folder.child("img$key")

            if(fileUri==null){
                val mVideoGame = ControllerProductos(name, date, price, description, imageUrl)
                myRef.setValue(mVideoGame)
            } else {
                videoGameReference.putFile(fileUri!!).addOnSuccessListener {
                    videoGameReference.downloadUrl.addOnSuccessListener { uri ->
                        val mVideoGame = ControllerProductos(name, date, price, description, uri.toString())
                        myRef.setValue(mVideoGame)
                    }
                }
            }

            finish()
        }

        bindingActivityEdit.posterImageView.setOnClickListener {
            fileUpload()
        }

    }

    private fun fileUpload() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        startActivityForResult(intent, file)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == file) {
            if (resultCode == RESULT_OK) {
                fileUri = data!!.data
                bindingActivityEdit.posterImageView.setImageURI(fileUri)
            }
        }
    }
}