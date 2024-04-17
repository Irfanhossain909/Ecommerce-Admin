package diploma_project_Admin.ecommerceadmin.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import diploma_project_Admin.ecommerceadmin.R
import diploma_project_Admin.ecommerceadmin.databinding.FragmentAddProductBinding


class AddProductFragment : Fragment() {
    private lateinit var binding: FragmentAddProductBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAddProductBinding.inflate(layoutInflater)




        return binding.root
    }

}