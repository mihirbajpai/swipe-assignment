package com.example.swipeassignment

import ProductAdapter
import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.swipeassignment.model.Product
import com.example.swipeassignment.network.RetrofitClient
import com.example.swipeassignment.repository.ProductRepository
import com.example.swipeassignment.viewmodel.ProductViewModel
import com.example.swipeassignment.viewmodel.ProductViewModelFactory
import java.util.Locale


class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var productAdapter: ProductAdapter
    private lateinit var viewModel: ProductViewModel
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var productList: List<Product>

    private var Permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)

//    private lateinit var networkChangeReceiver: NetworkChangeReceiver
//    private lateinit var connectivityStatusReceiver: BroadcastReceiver
//    private var isConnected: Boolean=true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        progressBar = findViewById(R.id.progressBar)
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)

        // Make progress bar visible
        progressBar.visibility = View.VISIBLE

        val repository = ProductRepository(RetrofitClient.productService)
        val viewModelFactory = ProductViewModelFactory(repository)

        viewModel = ViewModelProvider(this, viewModelFactory).get(ProductViewModel::class.java)

        viewModel.products.observe(this, Observer { products ->
            productList = products
            productAdapter = ProductAdapter(this, products)
            recyclerView.adapter = productAdapter
            hideLoading()
        })

        viewModel.fetchProducts()

        swipeRefreshLayout.setOnRefreshListener {
//            if(isConnected){
                showLoading()
                viewModel.fetchProducts()
//            }else{
//                swipeRefreshLayout.isRefreshing=false
//            }
        }

//        networkChangeReceiver = NetworkChangeReceiver()
//        registerReceiver(
//            networkChangeReceiver,
//            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
//        )
//
//        connectivityStatusReceiver = object : BroadcastReceiver() {
//            override fun onReceive(context: Context?, intent: Intent?) {
//                isConnected = intent?.getBooleanExtra("is_connected", false) ?: false
//                if (!isConnected) {
//                    // Show offline message
//                    Toast.makeText(this@MainActivity, "You are offline.", Toast.LENGTH_SHORT).show()
//                } else {
//                    // Hide offline message
//                    Toast.makeText(this@MainActivity, "You are online.", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
//        LocalBroadcastManager.getInstance(this)
//            .registerReceiver(connectivityStatusReceiver, IntentFilter("network_status"))

    }

    private fun showBottomSheetDialog() {
        val bottomSheetFragment = BottomSheetDialogFragment()
        bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)
    }

    //Action Bar
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        val searchItem = menu.findItem(R.id.action_search)
        val addItem = menu.findItem(R.id.action_add)
        val searchView = searchItem.actionView as SearchView?

        searchView!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                filterProductList(newText)
                return true
            }
        })



        addItem.setOnMenuItemClickListener {
//            showBottomSheetDialog()

            startActivity(Intent(this, UploadData::class.java))

            true
        }
        return true
    }


    //Search Item
    private fun filterProductList(query: String) {
        val filteredList: MutableList<Product> = ArrayList<Product>()
        for (product in productList) {
            if (product.product_name.toLowerCase().contains(query.lowercase(Locale.getDefault()))) {
                filteredList.add(product)
            }
        }
        if (productAdapter != null) {
            productAdapter.setFilter(filteredList)
        }
    }


    //Loading item
    private fun showLoading() {
        swipeRefreshLayout.isRefreshing = true
        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        swipeRefreshLayout.visibility = View.GONE
    }

    private fun hideLoading() {
        progressBar.visibility = View.GONE
        swipeRefreshLayout.isRefreshing = false
        progressBar.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
        swipeRefreshLayout.visibility = View.VISIBLE
    }

    fun getContext(): Context {
        return this;
    }

}