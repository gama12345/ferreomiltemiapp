package com.example.ferreteriaomiltemi.view.main

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ferreteriaomiltemi.databinding.FragmentMainOrdersBinding
import com.example.ferreteriaomiltemi.databinding.FragmentMainStartBinding
import com.example.ferreteriaomiltemi.domain.model.Order
import com.example.ferreteriaomiltemi.domain.model.Product
import com.example.ferreteriaomiltemi.domain.repository.AuthenticationRepository
import com.example.ferreteriaomiltemi.domain.repository.OrderRepository
import com.example.ferreteriaomiltemi.view.adapters.RecyclerAdapterOrderCart
import com.example.ferreteriaomiltemi.view.adapters.RecyclerAdapterOrders
import com.example.ferreteriaomiltemi.viewmodel.ClientViewModel
import com.example.ferreteriaomiltemi.viewmodel.OrderViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainOrdersFragment : Fragment() {
    private var _binding: FragmentMainOrdersBinding? = null
    private val binding get() = _binding!!
    lateinit var repository: AuthenticationRepository
    private val orderViewModel: OrderViewModel by activityViewModels()
    val adapterRV: RecyclerAdapterOrders = RecyclerAdapterOrders()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentMainOrdersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Repo
        repository = AuthenticationRepository(requireActivity().application)
        repository.firebaseUser.observe(viewLifecycleOwner, Observer { user ->
            if(user != null){
                getOrders()
            }
        })
        // ViewModel
        orderViewModel.orders.observe(viewLifecycleOwner, Observer { orders ->
            Log.d("UPDATED >>>> ", "UPDATED")
            setUpRecyclerView(orders)
            binding.progressBarOrders.visibility = View.GONE
            binding.rvOrders.visibility = View.VISIBLE
            binding.tvOrdersNoOrders.visibility = View.GONE
            if(orders.size == 0){
                binding.tvOrdersNoOrders.visibility = View.VISIBLE
            }
        })
    }

    private fun  getOrders(){
        binding.progressBarOrders.visibility = View.VISIBLE
        binding.rvOrders.visibility = View.GONE
        GlobalScope.launch {
            val id = repository.firebaseUser.value?.uid.toString()
            Log.d("DOCS >>> ", id+"  Ola")
            orderViewModel.getOrdersByClient(id)
        }
    }

    fun setUpRecyclerView(list: ArrayList<Order>){
        binding.rvOrders.setHasFixedSize(true)
        binding.rvOrders.layoutManager = LinearLayoutManager(requireContext())
        adapterRV.RecyclerAdapterOrders(list, requireContext(), orderViewModel)
        binding.rvOrders.adapter = adapterRV
    }
}