package com.admingrabs.firebasetutorial

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.admingrabs.firebasetutorial.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.net.URI

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseFireStore :FirebaseFirestore
    private lateinit var storageReference: StorageReference
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
        buttonClickEvents()
    }

    private fun buttonClickEvents() {
        binding.showAllBtn.setOnClickListener {
            startActivity(Intent(this,ImageActivity::class.java))
        }

        binding.uploadBtn.setOnClickListener {
            uploadImage()
        }
        binding.imageView.setOnClickListener {
            resultLaunchers.launch("image/*")
        }
    }

    private val resultLaunchers = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ){
        imageUri = it
        binding.imageView.setImageURI(it)
    }

    private fun init(){
        storageReference = FirebaseStorage.getInstance().reference.child("Images")
        firebaseFireStore = FirebaseFirestore.getInstance()
    }
    private fun uploadImage() {
        binding.progressBar.visibility = View.VISIBLE
        storageReference = storageReference.child(System.currentTimeMillis().toString())
        imageUri?.let {
            storageReference.putFile(it).addOnCompleteListener{ task ->
                if (task.isSuccessful) {

                    storageReference.downloadUrl.addOnSuccessListener {
                        val map = HashMap<String, Any>()
                        map["pic"] = it.toString()

                        firebaseFireStore.collection("images").add(map).addOnCompleteListener { fireStoreTask ->
                            if (fireStoreTask.isSuccessful) {
                                Toast.makeText(this, "Successfully Uploaded", Toast.LENGTH_LONG).show()
                            } else {
                                Toast.makeText(this, fireStoreTask.exception?.message, Toast.LENGTH_LONG).show()
                            }
                        }
                    }

                } else {
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
                    binding.progressBar.visibility = View.GONE
                    binding.imageView.setImageResource(R.drawable.upload)
                }

            }
        }
    }


}