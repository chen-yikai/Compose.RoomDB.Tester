package com.example.composeroomdbtester

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.launch

@Entity(tableName = "user")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0, @ColumnInfo(name = "name") val name: String
) {
    constructor() : this(0, "")
}

@Dao
interface UserDao {
    @Insert
    suspend fun insert(user: User)

    @Query("SELECT * FROM user")
    suspend fun getAllUsers(): List<User>

    @Delete
    suspend fun delete(user: User)

    @Query("DELETE FROM user")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM user WHERE name = :name")
    suspend fun checkNameExists(name: String): Int
}

@Database(entities = [User::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}


fun getDatabase(context: Context): AppDatabase {
    return Room.databaseBuilder(
        context.applicationContext, AppDatabase::class.java, "app_database"
    ).build()
}

class UserViewModel(private val database: AppDatabase) : ViewModel() {
    val users = mutableStateListOf<User>()

    init {
        updateUsers()
    }

    fun updateUsers() {
        viewModelScope.launch {
            users.clear()
            users.addAll(database.userDao().getAllUsers())
        }
    }

    fun addUser(name: String) {
        viewModelScope.launch {
            database.userDao().insert(User(name = name))
            updateUsers()
        }
    }

    fun deleteUser(user: User) {
        viewModelScope.launch {
            database.userDao().delete(user)
            updateUsers()
        }
    }

    fun deleteAllUsers() {
        viewModelScope.launch {
            database.userDao().deleteAll()
            updateUsers()
        }
    }

    suspend fun checkNameExists(name: String): Boolean {
        return database.userDao().checkNameExists(name) > 0
    }



}