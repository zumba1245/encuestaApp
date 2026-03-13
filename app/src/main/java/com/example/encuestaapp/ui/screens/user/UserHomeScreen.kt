package com.example.encuestaapp.ui.screens.user

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.encuestaapp.data.model.Survey
import com.example.encuestaapp.ui.screens.admin.AdminHomeUiState
import com.example.encuestaapp.ui.screens.admin.AdminHomeViewModel
import com.example.encuestaapp.ui.screens.admin.SurveyCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserHomeScreen(
    adminViewModel: AdminHomeViewModel = viewModel(),
    votingViewModel: VotingViewModel = viewModel()
) {
    val uiState = adminViewModel.uiState
    val selectedSurvey = votingViewModel.selectedSurvey
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(votingViewModel.errorMessage) {
        votingViewModel.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    LaunchedEffect(votingViewModel.voteSuccess) {
        if (votingViewModel.voteSuccess) {
            snackbarHostState.showSnackbar("¡Gracias por participar! 🗳️")
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Explorar", fontWeight = FontWeight.ExtraBold) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            AnimatedContent(targetState = uiState, label = "UserHomeContent") { state ->
                when (state) {
                    is AdminHomeUiState.Loading -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                    is AdminHomeUiState.Success -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            item { WelcomeHeader() }
                            items(state.surveys) { survey ->
                                SurveyCard(
                                    survey = survey,
                                    onClick = { votingViewModel.onSurveySelected(survey) }
                                )
                            }
                        }
                    }
                    is AdminHomeUiState.Empty -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No hay encuestas disponibles")
                        }
                    }
                    is AdminHomeUiState.Error -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Error: ${state.message}", color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }

        selectedSurvey?.let { survey ->
            VoteDialog(
                survey = survey,
                isVoting = votingViewModel.isVoting,
                onDismiss = { votingViewModel.dismissDialog() },
                onVote = { option: String -> votingViewModel.submitVote(option) }
            )
        }
    }
}

@Composable
fun VoteDialog(
    survey: Survey,
    isVoting: Boolean,
    onDismiss: () -> Unit,
    onVote: (String) -> Unit
) {
    var selectedOption by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier.padding(24.dp),
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text(survey.title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
                Text("Selecciona una opción", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(survey.question, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                
                survey.options.forEach { option ->
                    val isSelected = selectedOption == option
                    Surface(
                        onClick = { selectedOption = option },
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(selected = isSelected, onClick = { selectedOption = option })
                            Text(text = option, style = MaterialTheme.typography.bodyLarge, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { selectedOption?.let { onVote(it) } },
                enabled = selectedOption != null && !isVoting,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isVoting) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp, color = Color.White)
                } else {
                    Text("Confirmar Voto", fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                Text("Cerrar")
            }
        }
    )
}

@Composable
fun WelcomeHeader() {
    Column(modifier = Modifier.padding(bottom = 8.dp)) {
        Text(
            text = "Bienvenido de nuevo 👋",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Tu opinión transforma el futuro",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
