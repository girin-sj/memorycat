import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.random.Random

class MyViewModel : ViewModel() {
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val uid: String? = FirebaseAuth.getInstance().currentUser?.uid
    private val userDB = firestore.collection("userDB").document(uid!!)
    private val usedFieldNames = mutableListOf<String>()
    private val _level = MutableLiveData<String>()
    val level: LiveData<String> get() = _level
    private val _words: MutableLiveData<String> = MutableLiveData()
    val words: LiveData<String> get() = _words

    init {
        loadLevel()
    }

    private fun loadLevel() {
        userDB.get().addOnSuccessListener { document ->
            if (document != null) {
                _level.value = document.getString("level")
                Log.d("MyViewModel", "Level loaded: ${_level.value}")
            } else {
                Log.d("MyViewModel", "Document does not exist")
            }
        }.addOnFailureListener { exception ->
            Log.e("MyViewModel", "Error getting document: $exception")
        }
    }

    fun getWords() {
        val level = _level.value
        if (level == null) {
            Log.e("MyViewModel", "Level is null. Unable to get words.")
            return
        }

        val englishDictionaryCollection = firestore.collection("englishDictionary").document(level)
        englishDictionaryCollection.get()
            .addOnSuccessListener { dictionaryDocument ->
                if (dictionaryDocument != null) {
                    val fieldMap = dictionaryDocument.data
                    if (fieldMap != null) {
                        val fieldNames = fieldMap.keys.toList()
                        val availableFieldNames = fieldNames - usedFieldNames

                        if (availableFieldNames.isNotEmpty()) {
                            val randomFieldName = availableFieldNames.random()
                            usedFieldNames.add(randomFieldName)

                            // Update the value of _words LiveData
                            _words.value = randomFieldName
                            Log.d("MyViewModel", "New word: ${_words.value}")
                        }
                    }
                }
            }
    }
}


                    /*
                    private val users: MutableLiveData<List<User>> by lazy {
                        MutableLiveData<List<User>>().also {
                            loadUsers()
                        }
                    }

                    fun getUsers(): LiveData<List<User>> {
                        return users
                    }

                    private fun loadUsers() {
                        // Do an asynchronous operation to fetch users.
                    }
                    */