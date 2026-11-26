package com.kalkan.app

import com.kalkan.app.ui.screens.reports.ReportCategory
import com.kalkan.app.ui.screens.reports.ReportViewModel
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * İhbar formu ViewModel durum geçişlerini ve doğrulama kurallarını test eder.
 */
class ReportViewModelTest {

    @Test
    fun `kisa baslik veya aciklama girildiginde dogrulama hatasi doner`() = runBlocking {
        val viewModel = ReportViewModel()

        viewModel.onTitleChanged("Kısa")
        viewModel.onDescriptionChanged("Yeterli uzunlukta bir dolandırıcılık açıklaması metni.")

        viewModel.submitReport(
            errorTitleRes = 101,
            errorDescriptionRes = 102,
            errorGenericRes = 103
        )

        assertEquals(101, viewModel.uiState.value.validationErrorResId)
        assertFalse(viewModel.uiState.value.isSubmitted)
    }

    @Test
    fun `gecerli ihbar gonderildiginde basari durumu olusur ve form sifirlanir`() = runBlocking {
        val viewModel = ReportViewModel { category, title, _, _ ->
            assertEquals(ReportCategory.FRAUD_IBAN, category)
            assertEquals("Sahte kargo IBAN dolandırıcılığı", title)
            Result.success("rep_test_999")
        }

        viewModel.onCategorySelected(ReportCategory.FRAUD_IBAN)
        viewModel.onTitleChanged("Sahte kargo IBAN dolandırıcılığı")
        viewModel.onTargetValueChanged("TR120006100519786457841326")
        viewModel.onDescriptionChanged("Beni arayıp sahte kargo ücreti için IBAN paylaştılar.")

        viewModel.submitReport(
            errorTitleRes = 101,
            errorDescriptionRes = 102,
            errorGenericRes = 103
        )

        val state = viewModel.uiState.value
        assertTrue(state.isSubmitted)
        assertEquals("rep_test_999", state.lastCreatedReportId)
        assertEquals("", state.title)
        assertEquals("", state.description)
        assertNull(state.validationErrorResId)
        assertNotNull(state.lastCreatedReportId)
    }
}