package com.example.ui.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.CentreAnnouncement
import com.example.data.model.ChildProfile
import com.example.data.model.DiffImportResult
import com.example.data.model.HealthRecord
import com.example.data.model.NutritionStatus
import com.example.data.model.RationSchedule
import com.example.data.model.SessionRole
import com.example.data.model.UserSession
import com.example.data.model.VaccinationRecord
import com.example.data.remote.GrowthSummaryResult
import com.example.data.repository.ParentConnectRepository
import com.example.data.repository.ParentConnectRepositoryImpl
import com.example.ui.theme.AppLanguage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface DiffUiState {
    data object Idle : DiffUiState
    data object Parsing : DiffUiState
    data class DiffPreview(val diff: DiffImportResult) : DiffUiState
    data object Committing : DiffUiState
    data class Success(val count: Int, val fileName: String) : DiffUiState
    data class Error(val message: String) : DiffUiState
}

sealed interface AiAdviceState {
    data object Idle : AiAdviceState
    data object Loading : AiAdviceState
    data class Success(val summary: GrowthSummaryResult) : AiAdviceState
    data class Error(val message: String) : AiAdviceState
}

class PoshanViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ParentConnectRepository = ParentConnectRepositoryImpl.getInstance(application)

    // Language State (Trilingual: EN, MR, HI)
    private val _appLanguage = MutableStateFlow(AppLanguage.MARATHI) // Default to Marathi as per Maharashtra Anganwadi context
    val appLanguage: StateFlow<AppLanguage> = _appLanguage.asStateFlow()

    // Session State
    val userSession: StateFlow<UserSession> = repository.currentSession
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserSession())

    // Admin Auth Form Inputs
    private val _adminUsernameInput = MutableStateFlow("")
    val adminUsernameInput: StateFlow<String> = _adminUsernameInput.asStateFlow()

    private val _adminPinInput = MutableStateFlow("")
    val adminPinInput: StateFlow<String> = _adminPinInput.asStateFlow()

    // Parent Auth Form Inputs
    private val _parentMobileInput = MutableStateFlow("")
    val parentMobileInput: StateFlow<String> = _parentMobileInput.asStateFlow()

    private val _parentRegCodeInput = MutableStateFlow("")
    val parentRegCodeInput: StateFlow<String> = _parentRegCodeInput.asStateFlow()

    // Active Child (Strict isolation in Parent mode)
    private val _activeChildId = MutableStateFlow<String?>("AW-042-01")
    val activeChildId: StateFlow<String?> = _activeChildId.asStateFlow()

    private val _selectedChild = MutableStateFlow<ChildProfile?>(null)
    val selectedChild: StateFlow<ChildProfile?> = _selectedChild.asStateFlow()

    private val _childHealthRecords = MutableStateFlow<List<HealthRecord>>(emptyList())
    val childHealthRecords: StateFlow<List<HealthRecord>> = _childHealthRecords.asStateFlow()

    private val _childVaccinations = MutableStateFlow<List<VaccinationRecord>>(emptyList())
    val childVaccinations: StateFlow<List<VaccinationRecord>> = _childVaccinations.asStateFlow()

    // Services: Rations, Announcements, Attendance
    val rationSchedules: StateFlow<List<RationSchedule>> = repository.getRationSchedules()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val announcements: StateFlow<List<CentreAnnouncement>> = repository.getAnnouncements()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val attendanceMap: StateFlow<Map<String, Boolean>> = repository.getAttendanceMap()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    val allChildren: StateFlow<List<ChildProfile>> = repository.getAllChildren()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filter & Search in Admin roster
    private val _adminSearchQuery = MutableStateFlow("")
    val adminSearchQuery: StateFlow<String> = _adminSearchQuery.asStateFlow()

    private val _adminFilterStatus = MutableStateFlow<NutritionStatus?>(null)
    val adminFilterStatus: StateFlow<NutritionStatus?> = _adminFilterStatus.asStateFlow()

    val filteredChildren: StateFlow<List<ChildProfile>> = combine(
        allChildren,
        _adminSearchQuery,
        _adminFilterStatus,
    ) { list, query, _ ->
        list.filter { child ->
            query.isBlank() ||
                child.name.contains(query, ignoreCase = true) ||
                child.childId.contains(query, ignoreCase = true) ||
                child.guardian.contains(query, ignoreCase = true)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Bulk Excel Diff Engine State
    private val _diffState = MutableStateFlow<DiffUiState>(DiffUiState.Idle)
    val diffState: StateFlow<DiffUiState> = _diffState.asStateFlow()

    // AI Advice State
    private val _aiAdviceState = MutableStateFlow<AiAdviceState>(AiAdviceState.Idle)
    val aiAdviceState: StateFlow<AiAdviceState> = _aiAdviceState.asStateFlow()

    // SnackBar / Alert message
    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    init {
        // Preload default demo child data
        selectChild("AW-042-01")
    }

    fun setLanguage(lang: AppLanguage) {
        _appLanguage.value = lang
    }

    fun setAdminUsername(username: String) {
        _adminUsernameInput.value = username
    }

    fun setAdminPin(pin: String) {
        _adminPinInput.value = pin
    }

    fun setParentMobile(mobile: String) {
        _parentMobileInput.value = mobile
    }

    fun setParentRegCode(code: String) {
        _parentRegCodeInput.value = code
    }

    fun setAdminSearch(query: String) {
        _adminSearchQuery.value = query
    }

    fun setAdminFilterStatus(status: NutritionStatus?) {
        _adminFilterStatus.value = status
    }

    fun clearUserMessage() {
        _userMessage.value = null
    }

    // --- Authentication Actions ---

    fun loginAdmin() {
        viewModelScope.launch {
            val result = repository.loginAdmin(_adminUsernameInput.value, _adminPinInput.value)
            result.onSuccess {
                _userMessage.value = "Welcome, ${it.displayName}!"
            }.onFailure {
                _userMessage.value = it.message ?: "Admin Login Failed"
            }
        }
    }

    fun loginParent() {
        viewModelScope.launch {
            val result = repository.loginParent(_parentMobileInput.value, _parentRegCodeInput.value)
            result.onSuccess { session ->
                _userMessage.value = "Welcome, ${session.displayName}!"
                session.authenticatedChildId?.let { selectChild(it) }
            }.onFailure {
                _userMessage.value = it.message ?: "Authentication Failed"
            }
        }
    }

    fun logout() {
        repository.logout()
        _activeChildId.value = null
        _selectedChild.value = null
        _adminUsernameInput.value = ""
        _adminPinInput.value = ""
        _parentMobileInput.value = ""
        _parentRegCodeInput.value = ""
        _userMessage.value = "Logged out successfully"
    }

    // --- Quick Demo Fill Helpers ---

    fun fillAdminDemo() {
        _adminUsernameInput.value = "admin_aw042"
        _adminPinInput.value = "123456"
        loginAdmin()
    }

    fun fillParentDemo(mobile: String, code: String) {
        _parentMobileInput.value = mobile
        _parentRegCodeInput.value = code
        loginParent()
    }

    // --- Child Selection & Data Loading ---

    fun selectChild(childId: String) {
        _activeChildId.value = childId
        viewModelScope.launch {
            repository.getChildProfile(childId).collect { profile ->
                _selectedChild.value = profile
            }
        }
        viewModelScope.launch {
            repository.getChildHealthRecords(childId).collect { records ->
                _childHealthRecords.value = records
            }
        }
        viewModelScope.launch {
            repository.getChildVaccinations(childId).collect { vaxList ->
                _childVaccinations.value = vaxList
            }
        }
        loadAiAdvice(childId, forceRefresh = false)
    }

    fun loadAiAdvice(childId: String, forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _aiAdviceState.value = AiAdviceState.Loading
            try {
                val advice = repository.getAiGrowthAdvice(childId, forceRefresh)
                _aiAdviceState.value = AiAdviceState.Success(advice)
            } catch (e: Exception) {
                _aiAdviceState.value = AiAdviceState.Error(e.message ?: "Could not generate guidance")
            }
        }
    }

    // --- Daily Attendance Toggle ---

    fun toggleChildAttendance(childId: String, isPresent: Boolean) {
        viewModelScope.launch {
            repository.toggleAttendance(childId, isPresent)
        }
    }

    // --- Bulk Excel Import & Diff Engine ---

    fun parseUploadFile(uri: Uri, isExcel: Boolean) {
        viewModelScope.launch {
            _diffState.value = DiffUiState.Parsing
            try {
                val diffResult = repository.parseAndValidateUpload(uri, isExcel)
                _diffState.value = DiffUiState.DiffPreview(diffResult)
            } catch (e: Exception) {
                _diffState.value = DiffUiState.Error(e.message ?: "Failed to parse spreadsheet file")
            }
        }
    }

    fun parseSampleExcelTemplate() {
        viewModelScope.launch {
            _diffState.value = DiffUiState.Parsing
            try {
                val diffResult = repository.parseSampleExcel()
                _diffState.value = DiffUiState.DiffPreview(diffResult)
            } catch (e: Exception) {
                _diffState.value = DiffUiState.Error(e.message ?: "Error reading sample template")
            }
        }
    }

    fun commitDiffImport(diff: DiffImportResult) {
        viewModelScope.launch {
            _diffState.value = DiffUiState.Committing
            val result = repository.commitImport(diff)
            result.onSuccess { count ->
                _diffState.value = DiffUiState.Success(count, diff.fileName)
                _userMessage.value = "Import Committed: $count child records synchronized!"
            }.onFailure { err ->
                _diffState.value = DiffUiState.Error(err.message ?: "Failed to commit import")
            }
        }
    }

    fun resetDiffState() {
        _diffState.value = DiffUiState.Idle
    }

    fun getTemplateCsvText(): String = repository.getTemplateCsvContent()

    // --- Service Dispatch ---

    fun scheduleRation(date: String, timing: String, venue: String) {
        viewModelScope.launch {
            val schedule = RationSchedule(
                distributionDate = date,
                pickupTiming = timing,
                location = venue
            )
            repository.dispatchRationSchedule(schedule)
            _userMessage.value = "Ration Distribution Scheduled for $date"
        }
    }

    fun resetSeedData() {
        viewModelScope.launch {
            repository.resetDatabaseToInitial()
            _userMessage.value = "Demo records reset to default state"
            selectChild("AW-042-01")
        }
    }
}
