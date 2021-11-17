package com.example.ferreteriaomiltemi.view.client

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.ferreteriaomiltemi.R
import com.example.ferreteriaomiltemi.databinding.FragmentEditAccountBinding
import com.example.ferreteriaomiltemi.databinding.FragmentEditAccountEmailBinding
import com.example.ferreteriaomiltemi.domain.model.Client
import com.example.ferreteriaomiltemi.view.DataValidation
import com.example.ferreteriaomiltemi.viewmodel.AuthViewModel
import com.example.ferreteriaomiltemi.viewmodel.AuthViewModelFactory
import com.example.ferreteriaomiltemi.viewmodel.ClientViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

class EditAccountEmailFragment : Fragment() {
    private lateinit var _binding: FragmentEditAccountEmailBinding
    private val binding get() = _binding
    private lateinit var navController: NavController
    private lateinit var authViewModel: AuthViewModel
    private lateinit var authViewModelFactory: AuthViewModelFactory
    private val clientViewModel: ClientViewModel by activityViewModels()
    private val validator = DataValidation()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEditAccountEmailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Config toolbar
        setHasOptionsMenu(true)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.title = "Cambiar email"
        // Nav Controller
        navController = Navigation.findNavController(view)
        //ViewModels Instances
        authViewModelFactory = AuthViewModelFactory(requireActivity().application)
        authViewModel = ViewModelProvider(this, authViewModelFactory).get(AuthViewModel::class.java)
        // Events
        binding.btnEditAccountEmailSave.setOnClickListener { onClickSaveEmail() }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> navController.navigate(R.id.action_editAccountEmailFragment_to_editAccountFragment)
        }
        return super.onOptionsItemSelected(item)
    }

    fun onClickSaveEmail(){
        val newEmail = binding.etEditAccountEmailNewEmail.text.toString()
        val password = binding.etEditAccountEmailPassword.text.toString()
        val confirmPassword = binding.etEditAccountEmailConfirmPassword.text.toString()
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val encodedPassword = Base64.getEncoder().encodeToString(password.toByteArray())
            if(newEmail != clientViewModel.client.value?.email) {
                if (validator.isNotEmpty(newEmail)) {
                    if (validator.isValidEmail(newEmail)) {
                        if (validator.isNotEmpty(password)) {
                            if (validator.isNotEmpty(confirmPassword)) {
                                if (password == confirmPassword) {
                                    if (encodedPassword == clientViewModel.client.value?.password) {
                                        GlobalScope.launch(Dispatchers.Main) {
                                            val res = authViewModel.updateEmail(newEmail)
                                            if(res){
                                                val newData = Client(clientViewModel.client.value?.id.toString(),
                                                    clientViewModel.client.value?.name.toString(),
                                                    clientViewModel.client.value?.last_name.toString(),
                                                    clientViewModel.client.value?.phone.toString(),
                                                    newEmail,
                                                    clientViewModel.client.value?.password.toString())
                                                clientViewModel.updateClient(newData)
                                                cleanFields()
                                            }
                                        }
                                    } else Toast.makeText(activity,"Contraseña incorrecta", Toast.LENGTH_SHORT).show()
                                } else binding.etEditAccountEmailConfirmPassword.error =
                                    "Las contraseñas no coinciden"
                            } else binding.etEditAccountEmailConfirmPassword.error =
                                "Confirma tu contraseña para actualizar tu email"
                        } else binding.etEditAccountEmailPassword.error =
                            "Ingresa tu contraseña para actualizar tu email"
                    } else binding.etEditAccountEmailNewEmail.error =
                        "El formato de email no es reconodico"
                } else binding.etEditAccountEmailNewEmail.error = "No has especificado un email"
            }else binding.etEditAccountEmailNewEmail.error = "El nuevo email no puede ser identico al actual"
        } else {
            val toastMsg = "Su dispositivo no cuenta con la versón minima para ejecutar la app"
            Toast.makeText(requireActivity(), toastMsg, Toast.LENGTH_SHORT).show()
        }
    }

    fun cleanFields(){
        binding.etEditAccountEmailNewEmail.setText("")
        binding.etEditAccountEmailPassword.setText("")
        binding.etEditAccountEmailConfirmPassword.setText("")
    }
}