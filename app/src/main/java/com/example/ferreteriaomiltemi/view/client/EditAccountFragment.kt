package com.example.ferreteriaomiltemi.view.client

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.ferreteriaomiltemi.R
import com.example.ferreteriaomiltemi.databinding.FragmentEditAccountBinding
import com.example.ferreteriaomiltemi.databinding.FragmentLoginBinding
import com.example.ferreteriaomiltemi.databinding.FragmentViewAccountBinding
import com.example.ferreteriaomiltemi.domain.model.Client
import com.example.ferreteriaomiltemi.domain.model.NewClient
import com.example.ferreteriaomiltemi.domain.repository.AuthenticationRepository
import com.example.ferreteriaomiltemi.view.DataValidation
import com.example.ferreteriaomiltemi.view.main.MainActivity
import com.example.ferreteriaomiltemi.viewmodel.AuthViewModel
import com.example.ferreteriaomiltemi.viewmodel.AuthViewModelFactory
import com.example.ferreteriaomiltemi.viewmodel.ClientViewModel
import com.example.ferreteriaomiltemi.viewmodel.ClientViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class EditAccountFragment : Fragment() {
    private lateinit var _binding: FragmentEditAccountBinding
    private val binding get() = _binding
    private lateinit var navController: NavController
    private val clientViewModel: ClientViewModel by activityViewModels()
    private val validator = DataValidation()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEditAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Config toolbar
        setHasOptionsMenu(true)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.title = "Editar cuenta"
        // Nav Controller
        navController = Navigation.findNavController(view)
        // View model
        clientViewModel.client.observe(viewLifecycleOwner, Observer { client ->
            setClientData()
        })
        // Events
        binding.btnEditAccountCancel.setOnClickListener { navController.navigate(R.id.action_editAccountFragment_to_viewAccountFragment) }
        binding.btnEditAccountSave.setOnClickListener { onClickSave() }
        binding.imvEditAccountEditEmail.setOnClickListener{ navController.navigate(R.id.editAccountEmailFragment) }
        binding.imvEditAccountEditPassword.setOnClickListener{ navController.navigate(R.id.editAccountPasswordFragment) }
        // Set client data
        setClientData()

    }

    private fun setClientData(){
        binding.etEditAccountName.setText(clientViewModel.client.value?.name.toString())
        binding.etEditAccountLastName.setText(clientViewModel.client.value?.last_name.toString())
        binding.etEditAccountPhone.setText(clientViewModel.client.value?.phone.toString())
        binding.etEditAccountEmail.setText(clientViewModel.client.value?.email.toString())
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> {
                navController.navigate(R.id.action_editAccountFragment_to_viewAccountFragment)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    //OnClickEvents
    private fun onClickSave(){
        val name = binding.etEditAccountName.text.toString()
        val last_name = binding.etEditAccountLastName.text.toString()
        val phone = binding.etEditAccountPhone.text.toString()

        //Data Validation
        if(validator.isNotEmpty(name)){
            if(validator.isNotEmpty(last_name)){
                if(validator.isNotEmpty(phone)){
                    GlobalScope.launch(Dispatchers.Main) {
                        val newData = Client(clientViewModel.client.value?.id.toString(), name, last_name, phone,
                                            clientViewModel.client.value?.email.toString(),
                                            clientViewModel.client.value?.password.toString())
                        clientViewModel.updateClient(newData)
                    }
                }else binding.etEditAccountPhone.error = "No ha ingresado un teléfono"
            }else binding.etEditAccountLastName.error = "No ha especificado sus apellidos"
        }else binding.etEditAccountName.error = "No ha ingresado su nombre"
    }
}