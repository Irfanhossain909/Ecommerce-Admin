package diploma_project_Admin.ecommerceadmin.fragment

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import diploma_project_Admin.ecommerceadmin.R
import diploma_project_Admin.ecommerceadmin.adapter.CategoryAdapter
import diploma_project_Admin.ecommerceadmin.databinding.FragmentCategoryBinding
import diploma_project_Admin.ecommerceadmin.model.CategoryModel
import java.util.UUID

class CategoryFragment : Fragment() {
    private lateinit var binding: FragmentCategoryBinding
    private  var imageUrl: Uri? = null
    private lateinit var dialog: Dialog

    private var launchGalleryActivity = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){
        if(it.resultCode == Activity.RESULT_OK){
            imageUrl = it.data!!.data
            binding.imageView.setImageURI(imageUrl)
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCategoryBinding.inflate(layoutInflater)

        dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.progress_layout)
        dialog.setCancelable(false)

        getData()

        binding.apply {
            imageView.setOnClickListener {
                val intent = Intent("android.intent.action.GET_CONTENT")
                intent.type = "image/*"
                launchGalleryActivity.launch(intent)
            }
            button6.setOnClickListener {
                validateData(binding.categoryName.text.toString())
            }
        }





        return binding.root
    }

    private fun getData() {
        val list = ArrayList<CategoryModel>()
        Firebase.firestore.collection("category")
            .get().addOnSuccessListener {
                list.clear()
                for (doc in it.documents){
                    val data = doc.toObject(CategoryModel::class.java)
                    list.add(data!!)
                }
                binding.categoryRecycler.adapter = CategoryAdapter(requireContext(), list)
            }
    }

    private fun validateData(categoryName: String) {
        if (categoryName.isEmpty()){
            Toast.makeText(requireContext(), "Please provide category name", Toast.LENGTH_SHORT).show()
        }else if (imageUrl == null){
            Toast.makeText(requireContext(), "Please select image", Toast.LENGTH_SHORT).show()
        }else{
            uploadImage(categoryName)
        }
    }

    private fun uploadImage(categoryName: String) {
        dialog.show()

        val filename = UUID.randomUUID().toString()+"jpg"

        val refStorage = FirebaseStorage.getInstance().reference.child("category/$filename")
        refStorage.putFile(imageUrl!!)
            .addOnSuccessListener {
                it.storage.downloadUrl.addOnSuccessListener {image ->
                    storeData(categoryName, image.toString())
                }
            }
            .addOnFailureListener{
                dialog.dismiss()
                Toast.makeText(requireContext(),"Something went wrong with storage", Toast.LENGTH_SHORT).show()
            }
    }


    private fun storeData(categoryName: String,url: String){
        val db = Firebase.firestore

        val data = hashMapOf<String, Any>(
            "cate" to categoryName,
            "img" to url
        )
        db.collection("category")
            .add(data)
            .addOnSuccessListener {
                dialog.dismiss()
                binding.imageView.setImageDrawable(resources.getDrawable(R.drawable.pre_view))
                binding.categoryName.text =null
                getData()

                Toast.makeText(requireContext(),"Category Added",Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener{
                dialog.dismiss()
                Toast.makeText(requireContext(),"Something went Wrong",Toast.LENGTH_SHORT).show()
            }
    }


}