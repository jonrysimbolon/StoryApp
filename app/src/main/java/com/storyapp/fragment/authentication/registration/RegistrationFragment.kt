package com.storyapp.fragment.authentication.registration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.storyapp.R
import com.storyapp.databinding.FragmentRegistrationBinding
import com.storyapp.fragment.authentication.AuthenticationViewModel
import com.storyapp.main.MainViewModel
import com.storyapp.model.UserRegistrationModel
import com.storyapp.remote.response.ResultStatus
import com.storyapp.utils.doAnimation
import com.storyapp.utils.isValidRegistration
import com.storyapp.utils.showSnackBarAppearBriefly
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class RegistrationFragment : Fragment() {

    private val binding by lazy { FragmentRegistrationBinding.inflate(layoutInflater) }
    private val registrationViewModel: RegistrationViewModel by sharedViewModel()
    private val authViewModel: AuthenticationViewModel by sharedViewModel()
    private val mainViewModel: MainViewModel by sharedViewModel()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {

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
                        errorName = { getString(R.string.error_email) },
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
                    ).observe(viewLifecycleOwner) { result ->
                        when (result) {
                            ResultStatus.Loading -> mainViewModel.showLoading(true)

                            is ResultStatus.Error -> {
                                mainViewModel.showLoading(false)
                                result.error.showSnackBarAppearBriefly(root)
                            }

                            is ResultStatus.Success -> {
                                mainViewModel.showLoading(false)
                                result.data.message.showSnackBarAppearBriefly(root)
                                whatNext()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun whatNext() {
        authViewModel.switchToFragment(0)
    }
}