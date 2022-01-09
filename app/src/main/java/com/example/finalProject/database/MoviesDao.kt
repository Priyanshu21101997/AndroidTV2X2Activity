package com.example.finalProject.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.finalProject.repository.MoviesRepository

@Dao
interface MoviesDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addMovieToFavourites(movie:MoviesEntity)

    @Query("SELECT * from movies_table")
    fun readFavouriteMovies(): LiveData<List<MoviesEntity>>

    @Query("SELECT count(id) from movies_table where id = :custom_id  ")
    fun checkAlreadyFavourite(custom_id:Int):Int
}