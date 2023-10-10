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
    fun selectAll(): LiveData<List<Election>?>

    @Query("SELECT * FROM election_table WHERE id = :electionId")
    fun select(electionId: Int): Election?

    @Insert
    fun insert(election: Election)

    @Delete
    fun delete(election: Election)

    @Query("DELETE FROM election_table")
    fun clear()

}