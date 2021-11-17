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
import com.example.ferreteriaomiltemi.databinding.FragmentEditAccountEmailBinding
import com.example.ferreteriaomiltemi.databinding.FragmentEditAccountPasswordBinding
import com.example.ferreteriaomiltemi.domain.model.Client
import com.example.ferreteriaomiltemi.view.DataValidation
import com.example.ferreteriaomiltemi.viewmodel.AuthViewModel
import com.example.ferreteriaomiltemi.viewmodel.AuthViewModelFactory
import com.example.ferreteriaomiltemi.viewmodel.ClientViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

class EditAccountPasswordFragment : Fragment() {
    private lateinit var _binding: FragmentEditAccountPasswordBinding
    private val binding get() = _binding
    private lateinit var navController: NavController
    private lateinit var authViewModel: AuthViewModel
    private lateinit var authViewModelFactory: AuthViewModelFactory
    private val clientViewModel: ClientViewModel by activityViewModels()
    private val validator = DataValidation()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentEditAccountPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Config toolbar
        setHasOptionsMenu(true)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.title = "Cambiar contraseña"
        // Nav Controller
        navController = Navigation.findNavController(view)
        //ViewModels Instances
        authViewModelFactory = AuthViewModelFactory(requireActivity().application)
        authViewModel = ViewModelProvider(this, authViewModelFactory).get(AuthViewModel::class.java)
        // Events
        binding.btnEditAccountPasswordSave.setOnClickListener { onClickSavePassword() }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> navController.navigate(R.id.action_editAccountPasswordFragment_to_editAccountFragment)
        }
        return super.onOptionsItemSelected(item)
    }

    fun onClickSavePassword(){
        val newPassword = binding.etEditAccountPasswordNewPassword.text.toString()
        val confirmNewPassword = binding.etEditAccountPasswordConfirmNewPassword.text.toString()
        val currentPassword = binding.etEditAccountPasswordPassword.text.toString()
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            var encodedPassword = Base64.getEncoder().encodeToString(currentPassword.toByteArray())
            if(validator.isNotEmpty(newPassword)){
                if(validator.isNotEmpty(confirmNewPassword)){
                    if(newPassword == confirmNewPassword){
                        if(validator.isValidPasswordFormat(newPassword)){
                            if(validator.isNotEmpty(currentPassword)){
                                if(encodedPassword == clientViewModel.client.value?.password){
                                    GlobalScope.launch(Dispatchers.Main) {
                                        val res = authViewModel.updatePassword(newPassword)
                                        if(res){
                                            encodedPassword = Base64.getEncoder().encodeToString(newPassword.toByteArray())
                                            val newData = Client(clientViewModel.client.value?.id.toString(),
                                                    clientViewModel.client.value?.name.toString(),
                                                    clientViewModel.client.value?.last_name.toString(),
                                                    clientViewModel.client.value?.phone.toString(),
                                                    clientViewModel.client.value?.email.toString(),
                                                    encodedPassword)
                                            clientViewModel.updateClient(newData)
                                            cleanFields()
                                        }
                                    }
                                }else binding.etEditAccountPasswordPassword.error = "Contraseña incorrecta"
                            }else binding.etEditAccountPasswordPassword.error = "Ingrese la contraseña actual"
                        }else binding.etEditAccountPasswordNewPassword.error = "La contraseña debe tener al menos 6 caracteres e incluir una mayúscula y un número"
                    }else binding.etEditAccountPasswordConfirmNewPassword.error = "Las contraseñas no coinciden"
                }else binding.etEditAccountPasswordConfirmNewPassword.error = "Confirme la nueva contraseña"
            }else binding.etEditAccountPasswordNewPassword.error = "Ingrese la nueva contraseña"
        } else {
            val toastMsg = "Su dispositivo no cuenta con la versón minima para ejecutar la app"
            Toast.makeText(requireActivity(), toastMsg, Toast.LENGTH_SHORT).show()
        }
    }

    fun cleanFields(){
        binding.etEditAccountPasswordPassword.setText("")
        binding.etEditAccountPasswordNewPassword.setText("")
        binding.etEditAccountPasswordConfirmNewPassword.setText("")
    }
}