package com.nullo.openrouterclient.presentation.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.core.view.isVisible
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
        setupWebSearchSpinner()
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

                // Update web search mode
                val modeIndex = when (state.webSearchMode) {
                    "none" -> 0
                    "openrouter_online" -> 1
                    "brave" -> 2
                    "yandex" -> 3
                    "google" -> 4
                    "duckduckgo" -> 5
                    else -> 0
                }
                spinnerWebSearchMode.setSelection(modeIndex)

                // Show/hide Brave API key field
                val showBraveKey = state.webSearchMode == "brave"
                tilBraveApiKey.isVisible = showBraveKey
                tvBraveApiKeyHint.isVisible = showBraveKey
                btnSaveBraveKey.isVisible = showBraveKey
                etBraveApiKey.setText(state.braveApiKey)
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

            btnSaveBraveKey.setOnClickListener {
                val key = etBraveApiKey.text.toString().trim()
                if (key.isBlank()) {
                    Toast.makeText(context, R.string.error_blank_api_key, Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                viewModel.setBraveApiKey(key)
                Toast.makeText(context, R.string.saved, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setAppLocale(languageCode: String) {
        val localeList = LocaleListCompat.forLanguageTags(languageCode)
        AppCompatDelegate.setApplicationLocales(localeList)
    }

    private fun setupWebSearchSpinner() {
        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.web_search_modes,
            android.R.layout.simple_spinner_item
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        binding.spinnerWebSearchMode.adapter = adapter

        binding.spinnerWebSearchMode.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val mode = resources.getStringArray(R.array.web_search_modes_values)[position]
                viewModel.setWebSearchMode(mode)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
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
