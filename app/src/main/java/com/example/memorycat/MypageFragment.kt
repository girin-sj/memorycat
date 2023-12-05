package com.example.memorycat

import  com.example.memorycat.ViewModel.QuizViewModel
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.memorycat.ViewModel.MypageViewModel
import com.example.memorycat.databinding.FragmentMypageBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date

class MypageFragment : Fragment() {
    private var _binding: FragmentMypageBinding? = null
    private val binding get() = _binding!!

    private val quizViewModel: QuizViewModel by activityViewModels()
    private lateinit var mypageViewModel: MypageViewModel
    val localDate: LocalDate = LocalDate.now()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mypageViewModel = ViewModelProvider(requireActivity()).get(MypageViewModel::class.java)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMypageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val storage = FirebaseStorage.getInstance("gs://memorycat-c8f7d.appspot.com")
        val storageRef = storage.reference

        mypageViewModel.date.observe(viewLifecycleOwner) { date ->
            var imageIndex = date.toInt() - 1
            val imageView: ImageView = view.findViewById(R.id.img_stamp)
            val stampArray = resources.obtainTypedArray(R.array.stamp)
            if (imageIndex >= 7) {
                imageIndex = 0
            }
            if(mypageViewModel.checkDate(localDate) == false) {
                var newdate = date.toInt() + 1
                if (newdate == 8) {
                    Log.d("mypageFragment", "newdate: $newdate imageIndex: $imageIndex")
                    newdate = 1
                    Log.d("mypageFragment", "newdate: $newdate imageIndex: $imageIndex")
                }

                mypageViewModel.updateDate(newdate.toString())
                mypageViewModel.updateLocalDate(localDate.toString())
            }

            // change Stamp
            val imageResId =
                stampArray.getResourceId(imageIndex, -1) // get stamp image ID from array
            if (imageResId != -1) {
                imageView.setImageResource(imageResId)
            }

        }

        mypageViewModel.imageProfile.observe(viewLifecycleOwner) { imageProfile ->
            val imageAdress = "images/$imageProfile"
            val imageRef = storageRef.child(imageAdress)
            imageRef.downloadUrl
                .addOnSuccessListener { uri ->
                    // 이미지 로드 성공 시 Glide를 사용하여 이미지 로드
                    Glide.with(requireContext())
                        .load(uri)
                        .into(binding.accountIvProfile)
                }
                .addOnFailureListener { exception ->
                    // 이미지 로드 실패 시 처리
                    Toast.makeText(requireContext(), "이미지 로드 실패", Toast.LENGTH_SHORT).show()
                }
        }


        // get Level information from DB and display
        quizViewModel.level?.observe(viewLifecycleOwner) { level ->
            binding.userLevel.text = "Lv. ${level?.toUpperCase()}"
            when (level) {
                "bronze" -> binding.textLevelAdvice.text = "시작이 반이다!"
                "silver" -> binding.textLevelAdvice.text = "금메달을 향하여!"
                "gold" ->  binding.textLevelAdvice.text = "금메달 다음엔 뭘까요ㅎㅎ"
                "platium" ->  binding.textLevelAdvice.text = "한 단계만 더 해보자!"
                "master" ->  binding.textLevelAdvice.text = "영단어 박사님!!!"
            }
        }

        // get Goal information from DB and display
        mypageViewModel.goal.observe(viewLifecycleOwner) { goal ->
            binding.textUserGoal.text = "목표: ${goal?.toUpperCase()}"
        }

        // get Name information from DB and display
        mypageViewModel.name.observe(viewLifecycleOwner) { name ->
            binding.textUserName.text = "${name?.toUpperCase()}"
        }

        binding.buttonEditProfile.setOnClickListener {
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.replace(R.id.main_content, EditMypageFragment())
            transaction?.addToBackStack(null)
            transaction?.commit()
        }

        binding.buttonImageUpload.setOnClickListener {
            openGalleryForImage()
        }
    }

    // About firestore
    val getContent =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val selectedImageUri = result.data?.data
                selectedImageUri?.let { uri ->
                    uploadImageToFirestore(uri)
                    binding.accountIvProfile.setImageURI(uri) // change imageView
                }
            }
        }

    fun openGalleryForImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        getContent.launch(intent)
    }

    fun uploadImageToFirestore(imageUri: Uri) {
        val storageRef = FirebaseStorage.getInstance().reference
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageName = "IMAGE_" + timeStamp + "_.png"
        val imagesRef = storageRef.child("images/$imageName")

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
                mypageViewModel.updateProfileImage(imageName)
            } else {
                Log.e("MyPageViewModel", "Error uploading image: ${task.exception}")
            }
        }
    }

    fun saveImageUrlToFirestore(imageUrl: String) {
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