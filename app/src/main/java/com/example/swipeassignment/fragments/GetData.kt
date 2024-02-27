package com.example.swipeassignment.fragments

import com.example.swipeassignment.adapter.ProductAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.ColorStateList
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.swipeassignment.network.NetworkChangeReceiver
import com.example.swipeassignment.R
import com.example.swipeassignment.model.Product
import com.example.swipeassignment.viewmodel.ProductViewModel
import com.example.swipeassignment.viewmodel.ProductViewModelFactory
import java.util.Locale

class GetData : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var productAdapter: ProductAdapter
    private lateinit var viewModel: ProductViewModel
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var productList: List<Product>

    private lateinit var networkChangeReceiver: NetworkChangeReceiver
    private lateinit var connectivityStatusReceiver: BroadcastReceiver
    private var isConnected: Boolean = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_get_data, container, false)

        networkChangeReceiver = NetworkChangeReceiver()
        activity?.registerReceiver(
            networkChangeReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        )

        connectivityStatusReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                isConnected = intent?.getBooleanExtra("is_connected", false) ?: false
                if (!isConnected) {
                    Toast.makeText(requireContext(), "You are offline.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "You are online.", Toast.LENGTH_SHORT).show()
                    viewModel.fetchProducts()
                }
            }
        }

        LocalBroadcastManager.getInstance(requireContext())
            .registerReceiver(connectivityStatusReceiver, IntentFilter("network_status"))

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        progressBar = view.findViewById(R.id.progressBar)
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)

        val color = ContextCompat.getColor(requireContext(), R.color.black)

        progressBar.indeterminateTintList = ColorStateList.valueOf(color)
        progressBar.visibility = View.VISIBLE

        val viewModelFactory = ProductViewModelFactory(requireContext())
        viewModel = ViewModelProvider(this, viewModelFactory).get(ProductViewModel::class.java)

        if (isConnected) viewModel.products.observe(viewLifecycleOwner, Observer { products ->
            products?.let {
                productList = it
                productAdapter = ProductAdapter(requireContext(), it)
                recyclerView.adapter = productAdapter
                hideLoading()
            }
        })
        if (isConnected) {
            viewModel.fetchProducts()
        }

        swipeRefreshLayout.setOnRefreshListener {
            if (isConnected) {
                viewModel.fetchProducts()
                showLoading()
            } else {
                Toast.makeText(
                    requireContext(),
                    "You are offline. Please check your internet connection.",
                    Toast.LENGTH_SHORT
                ).show()
                swipeRefreshLayout.isRefreshing = false
            }
        }

        viewModel.response.observe(viewLifecycleOwner, Observer {
            if (it.isNotEmpty()) {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                viewModel.rest()
            }
        })

        viewModel.connectionError.observe(viewLifecycleOwner, Observer {
            if (it.isNotEmpty()) {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                viewModel.rest()
            }
        })

        setHasOptionsMenu(true)

        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
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
            val bottomSheetFragment = BottomSheetDialogFragment()
            bottomSheetFragment.show(requireFragmentManager(), bottomSheetFragment.tag)

            true
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun filterProductList(query: String) {
        val filteredList: MutableList<Product> = ArrayList<Product>()
        for (product in productList) {
            if (product.product_name.toLowerCase(Locale.getDefault())
                    .contains(query.lowercase(Locale.getDefault()))
            ) {
                filteredList.add(product)
            }
        }
        if (productAdapter != null) {
            productAdapter.setFilter(filteredList)
        }
    }

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

    override fun onDestroy() {
        super.onDestroy()
        activity?.unregisterReceiver(networkChangeReceiver)
        LocalBroadcastManager.getInstance(requireContext())
            .unregisterReceiver(connectivityStatusReceiver)
    }
}
