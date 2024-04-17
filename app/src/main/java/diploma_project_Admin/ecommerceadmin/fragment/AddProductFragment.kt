package diploma_project_Admin.ecommerceadmin.fragment

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.service.voice.VoiceInteractionSession.VisibleActivityCallback
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.transition.Visibility
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import diploma_project_Admin.ecommerceadmin.R
import diploma_project_Admin.ecommerceadmin.adapter.AddProductImageAdapter
import diploma_project_Admin.ecommerceadmin.databinding.FragmentAddProductBinding
import diploma_project_Admin.ecommerceadmin.model.AddProductModel
import diploma_project_Admin.ecommerceadmin.model.CategoryModel
import java.util.UUID


class AddProductFragment : Fragment() {
    private lateinit var binding: FragmentAddProductBinding
    private lateinit var list : ArrayList<Uri>
    private lateinit var listImage : ArrayList<String>
    private lateinit var adapter : AddProductImageAdapter
    private var coverImage : Uri? = null
    private lateinit var dialog : Dialog
    private var coverImgUri : String? = ""
    private lateinit var categoryList : ArrayList<String>

// for get cover Image
    private var launchGalleryActivity = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){
        if(it.resultCode == Activity.RESULT_OK){
            coverImage = it.data!!.data
            binding.productCoverImp.setImageURI(coverImage)
            binding.productCoverImp.visibility = View.VISIBLE//<- updated

        }
    }
// for get Product Image
    private var launchProductActivity = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){
        if(it.resultCode == Activity.RESULT_OK){
           val imageUrl = it.data!!.data
            list.add(imageUrl!!)
            adapter.notifyDataSetChanged()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAddProductBinding.inflate(layoutInflater)

        list = ArrayList()
        listImage = ArrayList()

        dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.progress_layout)
        dialog.setCancelable(false)

        binding.selectCoverImg.setOnClickListener {
            val intent = Intent("android.intent.action.GET_CONTENT")
            intent.type = "image/*"
            launchGalleryActivity.launch(intent)
        }

        binding.productImgBtn.setOnClickListener {
            val intent = Intent("android.intent.action.GET_CONTENT")
            intent.type = "image/*"
            launchProductActivity.launch(intent)
        }

        setProductCategory()


        adapter = AddProductImageAdapter(list)
        binding.productImgRecycalerview.adapter = adapter

        binding.submitProductBtn.setOnClickListener {
            validateData()
        }


        return binding.root
    }

    private fun validateData() {
        if (binding.productNameET.text.toString().isEmpty()){
           binding.productNameET.requestFocus()
            binding.productNameET.error = "Empty"
        }else if (binding.productSPET.text.toString().isEmpty()){
            binding.productSPET.requestFocus()
            binding.productSPET.error = "Empty"
        }else if (binding.productDescriptionET.text.toString().isEmpty()){
            binding.productDescriptionET.requestFocus()
            binding.productDescriptionET.error = "Empty"
        }else if (binding.productMrpET.text.toString().isEmpty()){
            binding.productMrpET.requestFocus()
            binding.productMrpET.error = "Empty"
        }else if (coverImage == null){
            Toast.makeText(requireContext(),"Please select cover image", Toast.LENGTH_SHORT).show()
        }else if (list.size <1){
            Toast.makeText(requireContext(),"Please select product image", Toast.LENGTH_SHORT).show()
        }else{
            uploadImage()
        }
    }

    private fun uploadImage() {
        dialog.show()

        val filename = UUID.randomUUID().toString()+"jpg"

        val refStorage = FirebaseStorage.getInstance().reference.child("products/$filename")
        refStorage.putFile(coverImage!!)
            .addOnSuccessListener {
                it.storage.downloadUrl.addOnSuccessListener {image ->
                   coverImgUri = image.toString()

                    uploadProductImage()
                }
            }
            .addOnFailureListener{
                dialog.dismiss()
                Toast.makeText(requireContext(),"Something went wrong with storage", Toast.LENGTH_SHORT).show()
            }
    }

    private var i = 0

    private fun uploadProductImage() {
        dialog.show()

        val filename = UUID.randomUUID().toString()+"jpg"

        val refStorage = FirebaseStorage.getInstance().reference.child("products/$filename")
        refStorage.putFile(list[i])
            .addOnSuccessListener {
                it.storage.downloadUrl.addOnSuccessListener {image ->
                    listImage.add(image!!.toString())
                    if (list.size == listImage.size){
                        storeData()
                    }else{
                        i += 1
                        uploadProductImage()
                    }


                }
            }
            .addOnFailureListener{
                dialog.dismiss()
                Toast.makeText(requireContext(),"Something went wrong with storage", Toast.LENGTH_SHORT).show()
            }
    }

    private fun storeData() {
        val db = Firebase.firestore.collection("products")
        val key = db.document().id
        val data = AddProductModel(
            binding.productNameET.text.toString(),
            binding.productDescriptionET.text.toString(),
            coverImgUri.toString(),
            categoryList[binding.productCategoryDropdown.selectedItemPosition],
            key,
            binding.productMrpET.text.toString(),
            binding.productSPET.text.toString(),
            listImage
        )

        db.document(key).set(data).addOnSuccessListener {
            dialog.dismiss()
            Toast.makeText(requireContext(), "Product Added", Toast.LENGTH_SHORT).show()
            binding.productNameET.text = null
            binding.productDescriptionET.text = null
            binding.productSPET.text = null
            binding.productMrpET.text = null
        }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setProductCategory(){
        categoryList = ArrayList()
        Firebase.firestore.collection("category").get().addOnSuccessListener {
            categoryList.clear()
            for (doc in it.documents){
                val data =  doc.toObject(CategoryModel::class.java)
                categoryList.add(data!!.cate!!)
            }
            categoryList.add(0,"Select Category")

            val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item_layout, categoryList)
            binding.productCategoryDropdown.adapter = arrayAdapter
        }
    }

}