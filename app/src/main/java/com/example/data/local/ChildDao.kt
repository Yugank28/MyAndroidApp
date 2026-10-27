package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.example.data.model.ChildProfile
import com.example.data.model.HealthRecord
import com.example.data.model.VaccinationRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface ChildDao {

    @Upsert
    suspend fun upsertProfile(profile: ChildProfile): Long

    @Upsert
    suspend fun upsertProfiles(profiles: List<ChildProfile>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHealthRecord(record: HealthRecord): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHealthRecords(records: List<HealthRecord>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVaccinations(records: List<VaccinationRecord>)

    @Query("SELECT * FROM child_profiles ORDER BY name ASC")
    fun getAllChildren(): Flow<List<ChildProfile>>

    @Query("SELECT * FROM child_profiles WHERE childId = :childId LIMIT 1")
    fun getChildById(childId: String): Flow<ChildProfile?>

    @Query("SELECT * FROM child_profiles WHERE childId = :childId LIMIT 1")
    suspend fun getChildByIdDirect(childId: String): ChildProfile?

    @Query("SELECT * FROM child_profiles WHERE parentContact LIKE '%' || :phone || '%'")
    fun getChildrenByPhone(phone: String): Flow<List<ChildProfile>>

    @Query("SELECT * FROM child_profiles WHERE (parentContact = :contact AND regCode = :regCode) OR childId = :query LIMIT 1")
    suspend fun verifyParentDirect(contact: String, regCode: String, query: String): ChildProfile?

    @Query("SELECT * FROM health_records WHERE childId = :childId ORDER BY date ASC")
    fun getHealthRecords(childId: String): Flow<List<HealthRecord>>

    @Query("SELECT * FROM health_records WHERE childId = :childId ORDER BY date DESC LIMIT 1")
    fun getLatestHealthRecord(childId: String): Flow<HealthRecord?>

    @Query("SELECT * FROM health_records WHERE childId = :childId ORDER BY date DESC LIMIT 1")
    suspend fun getLatestHealthRecordDirect(childId: String): HealthRecord?

    @Query("SELECT * FROM vaccination_records WHERE childId = :childId ORDER BY dueDate ASC")
    fun getVaccinations(childId: String): Flow<List<VaccinationRecord>>

    @Query("SELECT COUNT(*) FROM child_profiles")
    suspend fun getChildCount(): Int

    @Query("DELETE FROM child_profiles")
    suspend fun clearAllProfiles()

    @Query("DELETE FROM health_records")
    suspend fun clearAllHealthRecords()

    @Query("DELETE FROM vaccination_records")
    suspend fun clearAllVaccinations()
}
