package com.jonrysimbolonstory.fragment.authentication.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.jonrysimbolonstory.R
import com.jonrysimbolonstory.databinding.FragmentLoginBinding
import com.jonrysimbolonstory.model.UserLoginModel
import com.jonrysimbolonstory.remote.response.LoginResult
import com.jonrysimbolonstory.utils.LoadingDialog
import com.jonrysimbolonstory.utils.ResultStatus
import com.jonrysimbolonstory.utils.doAnimation
import com.jonrysimbolonstory.utils.isValidLogin
import com.jonrysimbolonstory.utils.showSnackBarAppearBriefly
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class LoginFragment : Fragment() {

    private val binding by lazy { FragmentLoginBinding.inflate(layoutInflater) }
    private val loginViewModel: LoginViewModel by activityViewModel()
    private val loadingDialog: LoadingDialog by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadingDialog.init(requireContext())

        binding.apply {
            loginViewModel.isValidLogin.observe(viewLifecycleOwner) { value ->
                value.getContentIfNotHandled()?.let { result ->
                    when (result) {
                        ResultStatus.Loading -> loadingDialog.show()

                        is ResultStatus.Error -> {
                            loadingDialog.show(false)
                            result.error.showSnackBarAppearBriefly(root)
                        }

                        is ResultStatus.Success -> {
                            loadingDialog.show(false)
                            whatNext(result.data.loginResult)
                        }
                    }
                }
            }

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
                    )
                }
            }
        }
    }

    private fun whatNext(result: LoginResult) {
        loginViewModel.saveUser(result)
    }
}