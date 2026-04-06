package com.nullo.openrouterclient.presentation.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.nullo.openrouterclient.R
import com.nullo.openrouterclient.databinding.FragmentSettingsBinding
import com.nullo.openrouterclient.presentation.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingsFragment : BottomSheetDialogFragment() {

    private val viewModel: MainViewModel by activityViewModels()

    private var _binding: FragmentSettingsBinding? = null
    private val binding
        get() = _binding ?: throw NullPointerException(
            "FragmentSettingsBinding is null."
        )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        observeViewModel()
        setupClickListeners()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                collectUiState()
            }
        }
    }

    private suspend fun collectUiState() {
        viewModel.uiState.collect { state ->
            with(binding) {
                etApiKey.setText(state.apiKey)
                btnClearChat.isEnabled = state.messages.isNotEmpty()

                // Update language selection
                rbEnglish.isChecked = state.language == "en"
                rbRussian.isChecked = state.language == "ru"
            }
        }
    }

    private fun setupClickListeners() {
        binding.apply {
            btnSaveKey.setOnClickListener {
                val apiKey = etApiKey.text.toString().trim()
                viewModel.setApiKey(apiKey)
            }

            btnClearChat.setOnClickListener {
                viewModel.clearChat()
                dismiss()
            }

            rgLanguage.setOnCheckedChangeListener { _, checkedId ->
                val language = when (checkedId) {
                    R.id.rb_english -> "en"
                    R.id.rb_russian -> "ru"
                    else -> return@setOnCheckedChangeListener
                }
                setAppLocale(language)
                viewModel.setLanguage(language)
                Toast.makeText(context, R.string.language_changed, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setAppLocale(languageCode: String) {
        val localeList = LocaleListCompat.forLanguageTags(languageCode)
        AppCompatDelegate.setApplicationLocales(localeList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

        const val TAG = "SettingsBottomSheet"

        fun newInstance(): SettingsFragment {
            return SettingsFragment()
        }
    }
}
