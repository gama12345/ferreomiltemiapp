package com.example.ferreteriaomiltemi.view.client

import android.app.Activity
import android.content.Intent
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.ferreteriaomiltemi.R
import com.example.ferreteriaomiltemi.databinding.FragmentViewAccountBinding
import com.example.ferreteriaomiltemi.domain.model.Client
import com.example.ferreteriaomiltemi.domain.repository.AuthenticationRepository
import com.example.ferreteriaomiltemi.view.main.MainActivity
import com.example.ferreteriaomiltemi.view.products.ProductsActivity
import com.example.ferreteriaomiltemi.viewmodel.ClientViewModel
import com.example.ferreteriaomiltemi.viewmodel.ClientViewModelFactory
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ViewAccountFragment : Fragment() {
    private lateinit var _binding: FragmentViewAccountBinding
    private val binding get() = _binding
    lateinit var repository: AuthenticationRepository
    private lateinit var navController: NavController
    private val clientViewModel: ClientViewModel by activityViewModels()
    private var extras: Bundle? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentViewAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Get previous activity
        extras = activity?.intent?.extras
        // Config toolbar
        setHasOptionsMenu(true)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.title = "Mi cuenta"
        // Repo
        repository = AuthenticationRepository(requireActivity().application)
        // ViewModel observers
        clientViewModel.client.observe(viewLifecycleOwner, Observer { client ->
            binding.tvViewAccountName.text = client.name+" "+client.last_name
            binding.tvViewAccountPhone.text = client.phone
            binding.tvViewAccountEmail.text = client.email
        })
        // Nav Controller
        navController = Navigation.findNavController(view)
        // Events
        binding.btnEditAccount.setOnClickListener { navController.navigate(R.id.editAccountFragment) }
        // Get client data
        binding.progressBarViewAccount.visibility = View.VISIBLE
        getClientData()
    }

    fun getClientData(){
        GlobalScope.launch(Dispatchers.Main) {
            val success = clientViewModel.setClientByEmail(repository.firebaseUser.value?.email.toString())
            if(success){
                binding.progressBarViewAccount.visibility = View.GONE
                binding.viewAccountLayout.visibility = View.VISIBLE
            }else {
                val snackMsg = "Ocurrió un error al obtener los datos de su cuenta"
                Snackbar.make(binding.viewAccountLayout, snackMsg, Snackbar.LENGTH_SHORT)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> {
                var intent = Intent(activity, MainActivity::class.java)
                if(extras?.get("previous") == "ProductsActivity") intent = Intent(activity, ProductsActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
                activity?.finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}