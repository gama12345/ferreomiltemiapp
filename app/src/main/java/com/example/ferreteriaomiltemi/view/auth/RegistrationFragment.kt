package com.example.ferreteriaomiltemi.view.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.ferreteriaomiltemi.R
import com.example.ferreteriaomiltemi.databinding.FragmentLoginBinding
import com.example.ferreteriaomiltemi.databinding.FragmentRegistrationBinding
import com.example.ferreteriaomiltemi.domain.model.NewClient
import com.example.ferreteriaomiltemi.view.DataValidation
import com.example.ferreteriaomiltemi.view.main.MainActivity
import com.example.ferreteriaomiltemi.viewmodel.AuthViewModel
import com.example.ferreteriaomiltemi.viewmodel.AuthViewModelFactory
import com.example.ferreteriaomiltemi.viewmodel.ClientViewModel
import com.example.ferreteriaomiltemi.viewmodel.ClientViewModelFactory
import kotlinx.coroutines.*


class RegistrationFragment : Fragment() {
    private var _binding: FragmentRegistrationBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController: NavController
    private val validator = DataValidation()
    private lateinit var authViewModel: AuthViewModel
    private lateinit var authViewModelFactory: AuthViewModelFactory
    private lateinit var clientViewModel: ClientViewModel
    private lateinit var clientViewModelFactory: ClientViewModelFactory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegistrationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //ViewModels Instances
        authViewModelFactory = AuthViewModelFactory(requireActivity().application)
        authViewModel = ViewModelProvider(this, authViewModelFactory).get(AuthViewModel::class.java)
        clientViewModelFactory = ClientViewModelFactory(requireActivity().application)
        clientViewModel = ViewModelProvider(this, clientViewModelFactory).get(ClientViewModel::class.java)
        //Livedata observers
        authViewModel.userData.observe(viewLifecycleOwner, Observer { user ->
            if(user != null){
                val intent = Intent(activity, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        })
        //Nav Controller
        navController = Navigation.findNavController(view)
        //Ataching events to UI
        binding.btnRegisterCancel.setOnClickListener{ navController.navigate(R.id.action_registrationFragment_to_loginFragment) }
        binding.btnRegisterRegister.setOnClickListener { onClickRegister() }

    }

    //OnClickEvents
    private fun onClickRegister(){
        val name = binding.etRegisterName.text.toString()
        val last_name = binding.etRegisterLastName.text.toString()
        val phone = binding.etRegisterPhone.text.toString()
        val email = binding.etRegisterEmail.text.toString()
        val password = binding.etRegisterPassword.text.toString()
        val confirmPassword = binding.etRegisterConfirmPassword.text.toString()

        //Data Validation
        if(validator.isNotEmpty(name)){
            if(validator.isNotEmpty(last_name)){
                if(validator.isNotEmpty(phone)){
                    if(validator.isNotEmpty(email)){
                        if(validator.isValidEmail(email)){
                            if(validator.isNotEmpty(password)){
                                if(validator.isValidPasswordFormat(password)) {
                                    if(password == confirmPassword){
                                        // Calling ViewModel to register client
                                        GlobalScope.launch(Dispatchers.Main) {
                                            val user = authViewModel.register(email, password)
                                            // User was created in auth
                                            if(user != null){
                                                //Register user data in firestore
                                                val newClient = NewClient(name, last_name, phone, email, password)
                                                val res = clientViewModel.register(newClient)
                                                if(res != null) {
                                                    authViewModel.login(email, password)
                                                    val toastMsg = "Te has registrado correctamente"
                                                    Toast.makeText(activity, toastMsg, Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        }
                                    }else binding.etRegisterConfirmPassword.error = "No hay coincidencia con la contraseña"
                                }else{
                                    binding.etRegisterPassword.error = "La contraseña debe tener al menos "+
                                            "6 caracteres incluyendo una mayúsucula y un número"
                                }
                            }else binding.etRegisterPassword.error = "No ha ingresado una contraseña"
                        }else binding.etRegisterEmail.error = "El formato de email no es reconocido"
                    }else binding.etRegisterEmail.error = "No ha ingresado un email"
                }else binding.etRegisterPhone.error = "No ha ingresado un teléfono"
            }else binding.etRegisterLastName.error = "No ha especificado sus apellidos"
        }else binding.etRegisterName.error = "No ha ingresado su nombre"
    }

}