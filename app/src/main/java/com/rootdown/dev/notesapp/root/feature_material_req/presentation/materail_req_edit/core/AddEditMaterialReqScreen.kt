package com.rootdown.dev.notesapp.root.feature_material_req.presentation.materail_req_edit.core

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.rootdown.dev.notesapp.R
import com.rootdown.dev.notesapp.root.feature_material_req.presentation.materail_req_edit.view_models.EditMaterialReqViewModel
import com.rootdown.dev.notesapp.root.feature_note.presentation.add_edit_note.components.TransparentHintTextField
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun AddEditMaterialReqScreen(
    navController: NavController,
    viewModel: EditMaterialReqViewModel = hiltViewModel()
){
    val phaseState = viewModel.reqPhase.value
    val quantityState = viewModel.reqQuantity.value
    val descriptionState = viewModel.reqDescription.value
    val nameState =  viewModel.reqMaterialName.value
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when(event){
                is EditMaterialReqViewModel.UIEvent.ShowSnackBar -> {
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = event.msg
                    )
                }
                is EditMaterialReqViewModel.UIEvent.SaveMaterialReq -> {
                    navController.navigateUp()
                }
            }
        }
    }

    Scaffold(
        floatingActionButton = {
            if (viewModel.currentMaterialReq == null) {
                FloatingActionButton(
                    onClick = {
                        viewModel.onEvent(AddEditMaterialReqEvent.SaveMaterialReq)
                        scope.launch {
                            scaffoldState.snackbarHostState.showSnackbar(
                                message = "Updated Material Req: ${nameState.text}"
                            )
                        }
                    },
                    backgroundColor = MaterialTheme.colors.primary
                ) {
                    Icon(imageVector = Icons.Default.Save, contentDescription = "Save MR")
                }
            } else {
                FloatingActionButton(
                    onClick = {
                        viewModel.onEvent(AddEditMaterialReqEvent.UpdateMaterialReq)
                        navController.navigateUp()
                    },
                    backgroundColor = MaterialTheme.colors.primary
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_baseline_update_24),
                        contentDescription = stringResource(id = R.string.login_details)
                    )
                }
            }

        },
        scaffoldState = scaffoldState
    ) {
        Column(
            horizontalAlignment =
            Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.DarkGray)
                .padding(10.dp)
        ){
            TransparentHintTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Transparent)
                    .padding(5.dp),
                text = if (viewModel.currentMaterialReq == null) phaseState.text else viewModel.currentMaterialReq!!.phase,
                hint = phaseState.hint,
                onValueChange = {
                    viewModel.onEvent(AddEditMaterialReqEvent.EnteredPhase(it))
                },
                onFocusChange = {
                    viewModel.onEvent(AddEditMaterialReqEvent.ChangePhaseFocus(it))
                },
                isHintVisible = if (viewModel.currentMaterialReq == null) phaseState.isHintVisible else false,
                singleLine = true,
                textStyle = MaterialTheme.typography.h5
            )
            Spacer(modifier = Modifier.height(16.dp))
            TransparentHintTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Transparent)
                    .padding(5.dp),
                text = if (viewModel.currentMaterialReq == null) quantityState.text else viewModel.currentMaterialReq!!.quantity,
                hint = quantityState.hint,
                onValueChange = {
                    viewModel.onEvent(AddEditMaterialReqEvent.EnteredQuantity(it))
                },
                onFocusChange = {
                    viewModel.onEvent(AddEditMaterialReqEvent.ChangeQuantityFocus(it))
                },
                isHintVisible = if (viewModel.currentMaterialReq == null) quantityState.isHintVisible else false,
                singleLine = true,
                textStyle = MaterialTheme.typography.h5
            )
            Spacer(modifier = Modifier.height(16.dp))
            TransparentHintTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Transparent)
                    .padding(5.dp),
                text = if (viewModel.currentMaterialReq == null) nameState.text else viewModel.currentMaterialReq!!.reqMaterial,
                hint = nameState.hint,
                onValueChange = {
                    viewModel.onEvent(AddEditMaterialReqEvent.EnteredReqName(it))
                },
                onFocusChange = {
                    viewModel.onEvent(AddEditMaterialReqEvent.ChangeReqNameFocus(it))
                },
                isHintVisible = if (viewModel.currentMaterialReq == null) nameState.isHintVisible else false,
                singleLine = true,
                textStyle = MaterialTheme.typography.h5
            )
            Spacer(modifier = Modifier.height(16.dp))
            TransparentHintTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Transparent)
                    .padding(5.dp),
                text = if (viewModel.currentMaterialReq == null) descriptionState.text else viewModel.currentMaterialReq!!.description,
                hint = descriptionState.hint,
                onValueChange = {
                    viewModel.onEvent(AddEditMaterialReqEvent.EnteredDescription(it))
                },
                onFocusChange = {
                    viewModel.onEvent(AddEditMaterialReqEvent.ChangeDescriptionFocus(it))
                },
                isHintVisible = if (viewModel.currentMaterialReq == null) descriptionState.isHintVisible else false,
                singleLine = true,
                textStyle = MaterialTheme.typography.h5
            )
        }
    }

}