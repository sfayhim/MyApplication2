package com.example.myapplication.ui.mammal

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.model.ClinToxResponseData
import com.example.myapplication.data.model.DtiResponseData
import com.example.myapplication.data.model.PharmaCompound
import com.example.myapplication.data.model.PpiResponseData
import com.example.myapplication.data.repository.OncologyRepository
import kotlinx.coroutines.launch

data class MammalUiState(
    val selectedTab: Int = 0, // 0: ClinTox, 1: DTI, 2: PPI, 3: Pharma Search
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    
    // ClinTox Inputs & Result
    val smilesInput: String = "CC(CCl)OC(C)CCl",
    val clinToxResult: ClinToxResponseData? = null,
    
    // DTI Inputs & Result
    val dtiTargetSeq: String = "NLMKRCTRGFRKLGKCTTLEEEKCKTLYPRGQCTCSDSKMNTHSCDCKSC",
    val dtiDrugSeq: String = "CC(=O)NCCC1=CNc2c1cc(OC)cc2",
    val dtiResult: DtiResponseData? = null,
    
    // PPI Inputs & Result
    val ppiProteinA: String = "MADQLTEEQIAEFKEAFSLFDKDGDGTITTKELGTVMRSLGQNPTEAELQDMINEVDADGNGTIDFPEFLTMMARKMKDTDSEEEIREAFRVFDKDGNGYISAAELRHVMTSLGEKLTDEEVDEMIREADIDGDGQVNYEEFVQMMTAK",
    val ppiProteinB: String = "MSSKLLLAGLDIEGVEILKTLSSKGKILEIQGIRLKEEAEEAKLKEEAEEAKLKEEAEEAK",
    val ppiResult: PpiResponseData? = null,
    
    // Pharma Inputs & Result
    val pharmaDrugInput: String = "Aspirin, Paclitaxel",
    val pharmaResults: List<PharmaCompound> = emptyList()
)

class MammalViewModel(
    private val repository: OncologyRepository = OncologyRepository()
) : ViewModel() {

    var uiState by mutableStateOf(MammalUiState())
        private set

    fun selectTab(tabIndex: Int) {
        uiState = uiState.copy(selectedTab = tabIndex, errorMessage = null)
    }

    fun onSmilesChanged(newSmiles: String) {
        uiState = uiState.copy(smilesInput = newSmiles)
    }

    fun onDtiTargetChanged(newTarget: String) {
        uiState = uiState.copy(dtiTargetSeq = newTarget)
    }

    fun onDtiDrugChanged(newDrug: String) {
        uiState = uiState.copy(dtiDrugSeq = newDrug)
    }

    fun onPpiAChanged(newSeq: String) {
        uiState = uiState.copy(ppiProteinA = newSeq)
    }

    fun onPpiBChanged(newSeq: String) {
        uiState = uiState.copy(ppiProteinB = newSeq)
    }

    fun onPharmaInputChanged(newInput: String) {
        uiState = uiState.copy(pharmaDrugInput = newInput)
    }

    fun predictClinTox() {
        if (uiState.smilesInput.isBlank()) return
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)
            val res = repository.predictClinTox(uiState.smilesInput)
            if (res.isSuccess) {
                uiState = uiState.copy(isLoading = false, clinToxResult = res.getOrNull())
            } else {
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = "Erreur lors de la prédiction ClinTox."
                )
            }
        }
    }

    fun predictDti() {
        if (uiState.dtiTargetSeq.isBlank() || uiState.dtiDrugSeq.isBlank()) return
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)
            val res = repository.predictDti(uiState.dtiTargetSeq, uiState.dtiDrugSeq)
            if (res.isSuccess) {
                uiState = uiState.copy(isLoading = false, dtiResult = res.getOrNull())
            } else {
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = "Erreur lors de la prédiction DTI."
                )
            }
        }
    }

    fun predictPpi() {
        if (uiState.ppiProteinA.isBlank() || uiState.ppiProteinB.isBlank()) return
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)
            val res = repository.predictPpi(uiState.ppiProteinA, uiState.ppiProteinB)
            if (res.isSuccess) {
                uiState = uiState.copy(isLoading = false, ppiResult = res.getOrNull())
            } else {
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = "Erreur lors de la prédiction PPI."
                )
            }
        }
    }

    fun searchPharma() {
        if (uiState.pharmaDrugInput.isBlank()) return
        val drugList = uiState.pharmaDrugInput.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)
            val res = repository.searchPharmaCompounds(drugList)
            if (res.isSuccess) {
                uiState = uiState.copy(
                    isLoading = false,
                    pharmaResults = res.getOrDefault(emptyList())
                )
            } else {
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = "Erreur lors de la recherche de composés pharmaceutiques."
                )
            }
        }
    }
}
