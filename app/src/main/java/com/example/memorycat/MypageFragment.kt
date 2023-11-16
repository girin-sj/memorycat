package com.example.memorycat

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.example.memorycat.databinding.FragmentMypageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.Date

class MypageFragment : Fragment() {
    private var _binding: FragmentMypageBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentMypageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
        val uid : String? = FirebaseAuth.getInstance().currentUser?.uid
        val userDB = firestore.collection("userDB").document(uid!!)
        userDB.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val level = document.getString("level")
                    binding.userLevel.text =  "Lv. ${level?.toUpperCase()}"
                }
                else {
                    Log.d("MypageFragment", "Document does not exist")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("MypageFragment", "Error getting document: $exception")
            }
        binding.buttonImageUpload.setOnClickListener {
            openGalleryForImage()
        }
    }
    private val getContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val selectedImageUri = result.data?.data
            selectedImageUri?.let { uri ->
                uploadImageToFirestore(uri)
            }
        }
    }
    private fun openGalleryForImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        getContent.launch(intent)
    }

    private fun uploadImageToFirestore(imageUri: Uri) {
        val storageRef = FirebaseStorage.getInstance().reference
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageName = "IMAGE_" + timeStamp + "_.png"
        val imagesRef = storageRef.child("images/${imageName}")

        val uploadTask = imagesRef.putFile(imageUri)

        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            imagesRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result // download URL of Uploaded image
                // Store downloadUri to Firestore
                saveImageUrlToFirestore(downloadUri.toString())
            } else {
                Toast.makeText(requireActivity(), "이미지 저장 실패", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveImageUrlToFirestore(imageUrl: String) {
        val imagesCollectionRef = FirebaseFirestore.getInstance().collection("images")
        val imageData = hashMapOf(
            "imageUrl" to imageUrl
        )

        imagesCollectionRef.add(imageData)
            .addOnSuccessListener { documentReference ->
                Log.d("Firestore", "Image URL added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error adding image URL", e)
            }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}