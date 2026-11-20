package com.kalkan.app.ui.screens.reports

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kalkan.app.R
import com.kalkan.app.notifications.NotificationHelper
import kotlinx.coroutines.launch

/**
 * TR: Umut — Kullanıcıların dolandırıcılık ve oltalama girişimlerini KVKK uyumlu bildirdiği ekran.
 * EN: Umut — Jetpack Compose screen for submitting phishing and fraud reports.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ReportScreen(
    viewModel: ReportViewModel = remember { ReportViewModel() },
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val notifSuccessTitle = stringResource(R.string.report_notification_title)
    val notifSuccessBody = stringResource(R.string.report_notification_body)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.report_screen_title)) }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(R.string.report_screen_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = stringResource(R.string.report_category_label),
                style = MaterialTheme.typography.titleSmall
            )

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                ReportCategory.entries.forEach { category ->
                    FilterChip(
                        selected = uiState.selectedCategory == category,
                        onClick = { viewModel.onCategorySelected(category) },
                        label = { Text(text = stringResource(categoryLabelRes(category))) }
                    )
                }
            }

            OutlinedTextField(
                value = uiState.title,
                onValueChange = viewModel::onTitleChanged,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text(text = stringResource(R.string.report_title_label)) },
                placeholder = { Text(text = stringResource(R.string.report_title_placeholder)) }
            )

            OutlinedTextField(
                value = uiState.targetValue,
                onValueChange = viewModel::onTargetValueChanged,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text(text = stringResource(R.string.report_target_label)) },
                placeholder = { Text(text = stringResource(R.string.report_target_placeholder)) }
            )

            OutlinedTextField(
                value = uiState.description,
                onValueChange = viewModel::onDescriptionChanged,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                label = { Text(text = stringResource(R.string.report_description_label)) },
                placeholder = { Text(text = stringResource(R.string.report_description_placeholder)) }
            )

            Text(
                text = stringResource(R.string.report_kvkk_notice),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            uiState.validationErrorResId?.let { errorRes ->
                Text(
                    text = stringResource(errorRes),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            if (uiState.isSubmitted) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = stringResource(R.string.report_success_title),
                            style = MaterialTheme.typography.titleSmall
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = stringResource(
                                R.string.report_success_message,
                                uiState.lastCreatedReportId.orEmpty()
                            ),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            Button(
                onClick = {
                    scope.launch {
                        viewModel.submitReport(
                            errorTitleRes = R.string.report_error_title_short,
                            errorDescriptionRes = R.string.report_error_description_short,
                            errorGenericRes = R.string.report_error_submit_failed
                        )
                        if (viewModel.uiState.value.isSubmitted) {
                            NotificationHelper.showNotification(
                                context = context,
                                title = notifSuccessTitle,
                                body = notifSuccessBody,
                                channelId = NotificationHelper.CHANNEL_REPORT_UPDATES
                            )
                        }
                    }
                },
                enabled = !uiState.isSubmitting,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (uiState.isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.height(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(text = stringResource(R.string.report_submit_button))
                }
            }
        }
    }
}

private fun categoryLabelRes(category: ReportCategory): Int {
    return when (category) {
        ReportCategory.PHISHING_LINK -> R.string.report_category_phishing_link
        ReportCategory.FRAUD_SMS -> R.string.report_category_fraud_sms
        ReportCategory.FRAUD_IBAN -> R.string.report_category_fraud_iban
        ReportCategory.FAKE_SITE -> R.string.report_category_fake_site
        ReportCategory.OTHER -> R.string.report_category_other
    }
}