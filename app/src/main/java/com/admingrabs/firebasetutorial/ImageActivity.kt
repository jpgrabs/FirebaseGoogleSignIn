package com.admingrabs.firebasetutorial

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.admingrabs.firebasetutorial.databinding.ActivityImageBinding
import com.google.firebase.firestore.FirebaseFirestore

class ImageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityImageBinding
    private lateinit var firebaseFireStore : FirebaseFirestore
    private var imageList = mutableListOf<String>()
    private lateinit var adapter: ImagesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)

        binding = ActivityImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
        displayTheImage()
    }

    private fun displayTheImage() {
        binding.progressBar.visibility = View.VISIBLE
        firebaseFireStore.collection("images")
            .get().addOnSuccessListener {
                for (i in it) {
                    imageList.add(i.data["pic"].toString())
                }
                adapter.notifyDataSetChanged()
                binding.progressBar.visibility = View.GONE
            }
    }

    private fun init() {
        firebaseFireStore = FirebaseFirestore.getInstance()
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ImagesAdapter(this,imageList)
        binding.recyclerView.adapter = adapter
    }
}