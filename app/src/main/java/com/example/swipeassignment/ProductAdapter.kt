import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.swipeassignment.R
import com.example.swipeassignment.model.Product

class ProductAdapter(private val context: Context, private var productList: List<Product>) :
    RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = productList[position]

        holder.textViewName.text = product.product_name
        holder.textViewPrice.text = "Price: ${product.price}$"
        holder.textViewType.text = "Type: ${product.product_type}"
        holder.textViewTax.text = "Tax: ${product.tax}%"

        // Load image using Glide
        Glide.with(context)
            .load(product.image)
            .placeholder(R.drawable.placeholder) // Placeholder image while loading
            .error(R.drawable.error_image) // Error image if loading fails
            .centerCrop()
            .into(holder.imageView)
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val textViewName: TextView = itemView.findViewById(R.id.textViewName)
        val textViewPrice: TextView = itemView.findViewById(R.id.textViewPrice)
        val textViewType: TextView = itemView.findViewById(R.id.textViewType)
        val textViewTax: TextView = itemView.findViewById(R.id.textViewTax)
    }

    fun setFilter(filteredList: List<Product>) {
        productList = filteredList
        notifyDataSetChanged()
    }
}
