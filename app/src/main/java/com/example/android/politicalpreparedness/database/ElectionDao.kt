package com.example.android.politicalpreparedness.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.android.politicalpreparedness.network.models.Election

@Dao
interface ElectionDao {

    @Query("SELECT * FROM election_table ORDER BY electionDay ASC")
    suspend fun selectAll(): LiveData<List<Election>>

    @Query("SELECT * FROM election_table WHERE id = :electionId")
    suspend fun select(electionId: Int): Election?

    @Insert
    suspend fun insert(election: Election)

    @Delete
    suspend fun delete(election: Election)

    @Query("DELETE FROM election_table")
    suspend fun clear()

}