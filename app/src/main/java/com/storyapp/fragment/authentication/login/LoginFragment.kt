package com.storyapp.fragment.authentication.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.storyapp.R
import com.storyapp.databinding.FragmentLoginBinding
import com.storyapp.main.MainViewModel
import com.storyapp.model.UserLoginModel
import com.storyapp.remote.response.LoginResult
import com.storyapp.remote.response.ResultStatus
import com.storyapp.utils.doAnimation
import com.storyapp.utils.isValidLogin
import com.storyapp.utils.showSnackBarAppearBriefly
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class LoginFragment : Fragment() {

    private val binding by lazy { FragmentLoginBinding.inflate(layoutInflater) }
    private val loginViewModel: LoginViewModel by activityViewModel()
    private val mainViewModel: MainViewModel by activityViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {

            doAnimation(
                ivLogin,
                loginBtn,
                tvLoginEmail,
                edLoginEmail,
                tvLoginPassword,
                edLoginPassword
            )

            loginBtn.setOnClickListener {
                if (isValidLogin(
                        emailBox = edLoginEmail,
                        passBox = edLoginPassword,
                        errorEmail = { getString(R.string.error_email) },
                        errorPass = { getString(R.string.error_password) }
                    )
                ) {
                    loginViewModel.login(
                        UserLoginModel(
                            edLoginEmail.text.toString(),
                            edLoginPassword.text.toString()
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
                                whatNext(result.data.loginResult)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun whatNext(result: LoginResult) {
        loginViewModel.saveUser(result)
    }
}