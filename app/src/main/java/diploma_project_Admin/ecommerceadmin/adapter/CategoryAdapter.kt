package diploma_project_Admin.ecommerceadmin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import diploma_project_Admin.ecommerceadmin.R
import diploma_project_Admin.ecommerceadmin.databinding.ItemCategoryLayoutBinding
import diploma_project_Admin.ecommerceadmin.model.CategoryModel

class CategoryAdapter(var context : Context, var list: ArrayList<CategoryModel>) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    inner class CategoryViewHolder(view : View) : RecyclerView.ViewHolder(view){
        var binding = ItemCategoryLayoutBinding.bind(view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder(LayoutInflater.from(context).inflate(R.layout.item_category_layout, parent, false ))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.binding.textView2.text = list[position].cate
        Glide.with(context).load(list[position].img).into(holder.binding.imageView2)
    }
}