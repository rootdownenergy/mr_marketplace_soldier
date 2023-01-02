package com.rootdown.dev.notesapp.root.feature_material_req.presentation.materail_req_edit.view_models

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseException
import com.rootdown.dev.notesapp.root.AppViewModel
import com.rootdown.dev.notesapp.root.di.util.DefaultDispatcher
import com.rootdown.dev.notesapp.root.di.util.IoDispatcher
import com.rootdown.dev.notesapp.root.feature_material_req.domain.model.MaterialReq
import com.rootdown.dev.notesapp.root.feature_material_req.domain.model.service.LogService
import com.rootdown.dev.notesapp.root.feature_material_req.domain.model.service.StorageMaterialReqService
import com.rootdown.dev.notesapp.root.feature_material_req.presentation.materail_req_edit.core.AddEditMaterialReqEvent
import com.rootdown.dev.notesapp.root.feature_material_req.presentation.materail_req_edit.core.MaterialReqTextFieldState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject



@HiltViewModel
class EditMaterialReqViewModel @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher,
    logService: LogService,
    private val materialReqStorageService: StorageMaterialReqService,
    savedStateHandle: SavedStateHandle
) : AppViewModel(logService) {

    var currentMaterialReq: MaterialReq? = null

    // material name state
    private val _reqMaterialName = mutableStateOf(MaterialReqTextFieldState(hint = "Enter Material Name: "))
    val reqMaterialName: State<MaterialReqTextFieldState> = _reqMaterialName

    // quantity state
    private val _reqQuantity = mutableStateOf(MaterialReqTextFieldState(hint = "Enter quantity requested: "))
    val reqQuantity: State<MaterialReqTextFieldState> = _reqQuantity

    // phase state
    private val _reqPhase = mutableStateOf(MaterialReqTextFieldState(hint = "Req for Phase: "))
    val reqPhase: State<MaterialReqTextFieldState> = _reqPhase

    // req description
    private val _reqDescription = mutableStateOf(MaterialReqTextFieldState(hint = "Req descriptions"))
    val reqDescription: State<MaterialReqTextFieldState> = _reqDescription


    private val _eventFlow = MutableSharedFlow<UIEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        savedStateHandle.get<String>("reqId")?.let { reqId ->
            if (reqId.toString().isNotBlank() && reqId != "HTW") {
                viewModelScope.launch(ioDispatcher) {
                    Log.w("XXX", "In coroutine to get material req from saved state handle reqId")
                    Log.w("XXX", "In coroutine reqId: ${reqId.toString()}")
                    currentMaterialReq = materialReqStorageService.getMR(reqId.toString())
                    Log.w("XXX", "Current Re: ${currentMaterialReq.toString()}")
                    _reqPhase.value = reqPhase.value.copy(
                        text = currentMaterialReq!!.phase,
                        isHintVisible = false
                    )
                    _reqQuantity.value = reqQuantity.value.copy(
                        text = currentMaterialReq!!.quantity,
                        isHintVisible = false
                    )
                    _reqMaterialName.value = reqMaterialName.value.copy(
                        text = currentMaterialReq!!.reqMaterial,
                        isHintVisible = false
                    )
                    _reqDescription.value = reqDescription.value.copy(
                        text = currentMaterialReq!!.description,
                        isHintVisible = false
                    )
                }
            }
        }
    }

    fun onEvent(event: AddEditMaterialReqEvent) {
        when(event) {
            is AddEditMaterialReqEvent.EnteredPhase -> {
                _reqPhase.value = reqPhase.value.copy(
                    text = event.value
                )
            }
            is AddEditMaterialReqEvent.ChangePhaseFocus -> {
                if (currentMaterialReq == null) {
                    _reqPhase.value = _reqPhase.value.copy(
                        isHintVisible = !event.focusState.isFocused &&
                                reqPhase.value.text.isBlank()
                    )
                } else {
                    _reqPhase.value = _reqPhase.value.copy(
                        isHintVisible = false
                    )
                }

            }
            is AddEditMaterialReqEvent.EnteredQuantity -> {
                _reqQuantity.value = reqQuantity.value.copy(
                    text = event.value
                )
            }
            is AddEditMaterialReqEvent.ChangeQuantityFocus -> {
                if (currentMaterialReq == null) {
                    _reqQuantity.value = _reqQuantity.value.copy(
                        isHintVisible = !event.focusState.isFocused &&
                                reqQuantity.value.text.isBlank()
                    )
                } else {
                    _reqQuantity.value = _reqQuantity.value.copy(
                        isHintVisible = false
                    )
                }
            }
            is AddEditMaterialReqEvent.EnteredReqName -> {
                _reqMaterialName.value = reqMaterialName.value.copy(
                    text = event.value
                )
            }
            is AddEditMaterialReqEvent.ChangeReqNameFocus -> {
                if (currentMaterialReq == null) {
                    _reqMaterialName.value = _reqMaterialName.value.copy(
                        isHintVisible = !event.focusState.isFocused &&
                                reqMaterialName.value.text.isBlank()
                    )
                } else {
                    _reqMaterialName.value = _reqMaterialName.value.copy(
                        isHintVisible = false
                    )
                }
            }
            is AddEditMaterialReqEvent.EnteredDescription -> {
                _reqDescription.value = reqMaterialName.value.copy(
                    text = event.value
                )
            }
            is AddEditMaterialReqEvent.ChangeDescriptionFocus -> {
                if (currentMaterialReq == null) {
                    _reqDescription.value = _reqDescription.value.copy(
                        isHintVisible = !event.focusState.isFocused &&
                                reqMaterialName.value.text.isBlank()
                    )
                } else {
                    _reqDescription.value = _reqDescription.value.copy(
                        isHintVisible = false
                    )
                }
            }
            is AddEditMaterialReqEvent.SaveMaterialReq -> {
                try {
                    viewModelScope.launch(ioDispatcher) {
                        val materialReq: MaterialReq = MaterialReq(
                            backlogMR = false,
                            bulkWeekMR = true,
                            dayMR = false,
                            description = reqDescription.value.text,
                            phase = reqPhase.value.text,
                            quantity = reqQuantity.value.text,
                            reqMaterial = reqMaterialName.value.text,
                            completed = true
                        )
                        materialReqStorageService.save(materialReq)
                        _eventFlow.emit(UIEvent.SaveMaterialReq)
                    }
                } catch (e: Exception) {
                    viewModelScope.launch(defaultDispatcher) {
                        _eventFlow.emit(
                            UIEvent.ShowSnackBar(
                                msg = e.message ?: "Unknown Error"
                            )
                        )
                    }
                }
            }
            is AddEditMaterialReqEvent.UpdateMaterialReq -> {
                try {
                    viewModelScope.launch(ioDispatcher) {
                        val materialReq: MaterialReq = MaterialReq(
                            reqId = currentMaterialReq!!.reqId,
                            backlogMR = false,
                            bulkWeekMR = true,
                            dayMR = false,
                            description = currentMaterialReq!!.description,
                            phase = currentMaterialReq!!.phase,
                            quantity = currentMaterialReq!!.quantity,
                            reqMaterial = currentMaterialReq!!.reqMaterial,
                            completed = true
                        )
                        Log.w("YYY", "Current Req: ${materialReq.toString()}")
                        materialReqStorageService.update(materialReq)
                    }
                } catch (ex: FirebaseException) {
                    viewModelScope.launch(defaultDispatcher) {
                        _eventFlow.emit(
                            UIEvent.ShowSnackBar(
                                msg = ex.message ?: "Unknown Error"
                            )
                        )
                    }
                }
            }
        }
    }
    sealed class UIEvent {
        data class ShowSnackBar(val msg: String): UIEvent()
        object SaveMaterialReq: UIEvent()
    }
}