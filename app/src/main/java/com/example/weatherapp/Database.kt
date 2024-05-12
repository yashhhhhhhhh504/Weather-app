package com.example.weatherapp
import android.app.Application
import android.content.Context
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
@Entity
data class PastData(
    @PrimaryKey val date: String,
    val minTemp: Double,
    val maxTemp: Double,
)

@Dao
interface PastDataDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(pastData: PastData)
    @Query("SELECT * FROM PastData")
    fun getPastData(): Flow<List<PastData>>
}

@Database(entities = [PastData::class], version = 1, exportSchema = false)
public abstract class PastDataDatabase : RoomDatabase() {
    abstract fun pastDataDao(): PastDataDao
    companion object {
        @Volatile
        private var INSTANCE: PastDataDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): PastDataDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PastDataDatabase::class.java,
                    "past_data_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

class PastRepository(private val pastDataDao: PastDataDao) {
    val allPastData: Flow<List<PastData>> = pastDataDao.getPastData()
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(pastData: PastData) {
        pastDataDao.insert(pastData)
    }
}

class PastViewModel(private val repository: PastRepository) : ViewModel() {
    val allPastData: LiveData<List<PastData>> = repository.allPastData.asLiveData()
    fun insert(pastData: PastData) = viewModelScope.launch {
        repository.insert(pastData)
    }
}

class PastViewModelFactory(private val repository: PastRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PastViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PastViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class PastApplication : Application() {
    val applicationScope = CoroutineScope(SupervisorJob())
    val database by lazy { PastDataDatabase.getDatabase(this, applicationScope) }
    val repository by lazy { PastRepository(database.pastDataDao()) }
}