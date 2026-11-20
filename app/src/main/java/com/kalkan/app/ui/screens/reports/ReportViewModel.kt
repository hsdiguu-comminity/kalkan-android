package com.kalkan.app.ui.screens.reports

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * TR: Umut — Backend reports modülüyle uyumlu ihbar kategorileri.
 * EN: Umut — Report categories aligned with backend reports module.
 */
enum class ReportCategory(val apiValue: String) {
    PHISHING_LINK("phishing_link"),
    FRAUD_SMS("fraud_sms"),
    FRAUD_IBAN("fraud_iban"),
    FAKE_SITE("fake_site"),
    OTHER("other")
}

/**
 * TR: Umut — İhbar ekranının arayüz durum modeli (UDF - Unidirectional Data Flow).
 * EN: Umut — UI state model for the report submission screen.
 */
data class ReportUiState(
    val selectedCategory: ReportCategory = ReportCategory.PHISHING_LINK,
    val title: String = "",
    val description: String = "",
    val targetValue: String = "",
    val isSubmitting: Boolean = false,
    val isSubmitted: Boolean = false,
    val validationErrorResId: Int? = null,
    val lastCreatedReportId: String? = null
)

/**
 * TR: Umut — İhbar formunun doğrulama, KVKK maskeleme önizlemesi ve gönderim mantığını yöneten ViewModel.
 * EN: Umut — ViewModel managing report validation, KVKK masking preview, and submission flow.
 */
class ReportViewModel(
    private val submitHandler: suspend (
        category: ReportCategory,
        title: String,
        description: String,
        targetValue: String
    ) -> Result<String> = { _, _, _, _ ->
        Result.success("rep_${System.currentTimeMillis()}")
    }
) {
    private val _uiState = MutableStateFlow(ReportUiState())
    val uiState: StateFlow<ReportUiState> = _uiState.asStateFlow()

    fun onCategorySelected(category: ReportCategory) {
        _uiState.update {
            it.copy(
                selectedCategory = category,
                validationErrorResId = null,
                isSubmitted = false
            )
        }
    }

    fun onTitleChanged(newTitle: String) {
        _uiState.update {
            it.copy(
                title = newTitle.take(120),
                validationErrorResId = null,
                isSubmitted = false
            )
        }
    }

    fun onDescriptionChanged(newDescription: String) {
        _uiState.update {
            it.copy(
                description = newDescription.take(1000),
                validationErrorResId = null,
                isSubmitted = false
            )
        }
    }

    fun onTargetValueChanged(newTarget: String) {
        _uiState.update {
            it.copy(
                targetValue = newTarget.take(250),
                validationErrorResId = null,
                isSubmitted = false
            )
        }
    }

    /**
     * TR: Form alanlarını doğrular; hata varsa ilgili string resource ID döner, geçerliyse null döner.
     * EN: Validates form fields; returns string resource ID on error or null when valid.
     */
    fun validateForm(
        errorTitleRes: Int,
        errorDescriptionRes: Int
    ): Int? {
        val current = _uiState.value
        if (current.title.trim().length < 5) {
            _uiState.update { it.copy(validationErrorResId = errorTitleRes) }
            return errorTitleRes
        }
        if (current.description.trim().length < 15) {
            _uiState.update { it.copy(validationErrorResId = errorDescriptionRes) }
            return errorDescriptionRes
        }
        return null
    }

    /**
     * TR: İhbarı backend'e gönderir ve başarılı olursa formu sıfırlayıp onay mesajı gösterir.
     * EN: Submits the report to backend and resets the form on success.
     */
    suspend fun submitReport(
        errorTitleRes: Int,
        errorDescriptionRes: Int,
        errorGenericRes: Int
    ) {
        if (validateForm(errorTitleRes, errorDescriptionRes) != null) return

        val snapshot = _uiState.value
        _uiState.update { it.copy(isSubmitting = true, validationErrorResId = null) }

        val result = submitHandler(
            snapshot.selectedCategory,
            snapshot.title.trim(),
            snapshot.description.trim(),
            snapshot.targetValue.trim()
        )

        result.fold(
            onSuccess = { createdId ->
                _uiState.update {
                    ReportUiState(
                        selectedCategory = snapshot.selectedCategory,
                        isSubmitted = true,
                        lastCreatedReportId = createdId
                    )
                }
            },
            onFailure = {
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        validationErrorResId = errorGenericRes
                    )
                }
            }
        )
    }

    fun dismissSuccessBanner() {
        _uiState.update { it.copy(isSubmitted = false, lastCreatedReportId = null) }
    }
}