package com.jonrysimbolonstory.fragment.authentication.registration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.jonrysimbolonstory.R
import com.jonrysimbolonstory.databinding.FragmentRegistrationBinding
import com.jonrysimbolonstory.fragment.authentication.AuthenticationViewModel
import com.jonrysimbolonstory.model.UserRegistrationModel
import com.jonrysimbolonstory.utils.LoadingDialog
import com.jonrysimbolonstory.utils.ResultStatus
import com.jonrysimbolonstory.utils.doAnimation
import com.jonrysimbolonstory.utils.isValidRegistration
import com.jonrysimbolonstory.utils.showSnackBarAppearBriefly
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class RegistrationFragment : Fragment() {

    private val binding by lazy { FragmentRegistrationBinding.inflate(layoutInflater) }
    private val registrationViewModel: RegistrationViewModel by activityViewModel()
    private val authViewModel: AuthenticationViewModel by activityViewModel()
    private val loadingDialog: LoadingDialog by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadingDialog.init(requireContext())
        binding.apply {

            registrationViewModel.isValidRegister.observe(viewLifecycleOwner) { result ->
                when (result) {
                    ResultStatus.Loading -> loadingDialog.show()

                    is ResultStatus.Error -> {
                        loadingDialog.show(false)
                        result.error.showSnackBarAppearBriefly(root)
                    }

                    is ResultStatus.Success -> {
                        loadingDialog.show(false)
                        result.data.message.showSnackBarAppearBriefly(root)
                        whatNext()
                    }
                }
            }

            doAnimation(
                ivRegistration,
                registrationBtn,
                tvRegisName,
                edRegisterName,
                tvRegisEmail,
                edRegisterEmail,
                tvRegisPassword,
                edRegisterPassword
            )

            registrationBtn.setOnClickListener {
                if (isValidRegistration(
                        nameBox = edRegisterName,
                        emailBox = edRegisterEmail,
                        passBox = edRegisterPassword,
                        errorName = { getString(R.string.error_name) },
                        errorEmail = { getString(R.string.error_email) },
                        errorPass = { getString(R.string.error_password) }
                    )
                ) {
                    registrationViewModel.register(
                        UserRegistrationModel(
                            edRegisterName.text.toString(),
                            edRegisterEmail.text.toString(),
                            edRegisterPassword.text.toString()
                        )
                    )
                }
            }
        }
    }

    private fun whatNext() {
        authViewModel.switchToFragment(0)
    }
}