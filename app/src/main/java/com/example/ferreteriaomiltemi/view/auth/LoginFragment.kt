package com.example.ferreteriaomiltemi.view.auth

import android.content.Intent
import android.os.Bundle
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
import com.example.ferreteriaomiltemi.view.DataValidation
import com.example.ferreteriaomiltemi.view.main.MainActivity
import com.example.ferreteriaomiltemi.viewmodel.AuthViewModel
import com.example.ferreteriaomiltemi.viewmodel.AuthViewModelFactory
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {
    private lateinit var _binding: FragmentLoginBinding
    private val binding get() = _binding
    private lateinit var navController:NavController
    private lateinit var authViewModel: AuthViewModel
    private lateinit var authViewModelFactory: AuthViewModelFactory
    private val validator = DataValidation()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        authViewModelFactory = AuthViewModelFactory(requireActivity().application)
        authViewModel = ViewModelProvider(this, authViewModelFactory).get(AuthViewModel::class.java)
        navController = Navigation.findNavController(view)
        binding.btnLogin.setOnClickListener { onClickLogin() }
        binding.tvLoginGoRegistration.setOnClickListener{ navController.navigate(R.id.registrationFragment) }
        authViewModel.userData.observe(viewLifecycleOwner, Observer { user ->
            if(user != null){
                val intent = Intent(activity, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        })
    }

    private fun onClickLogin(){
        val email = binding.etLoginEmail.text.toString()
        val password = binding.etLoginPassword.text.toString()
        if(validator.isNotEmpty(email)) {
            if(validator.isNotEmpty(password)){
                if(validator.isValidEmail(email)){
                    GlobalScope.launch { authViewModel.login(email, password) }
                }else{
                    binding.etLoginEmail.error = "Formato de email no reconocido"
                }
            }else{
                binding.etLoginPassword.error = "No ha ingresado una contraseña"
            }
        }else{
            binding.etLoginEmail.error = "No ha ingresado un email"
        }
    }
}