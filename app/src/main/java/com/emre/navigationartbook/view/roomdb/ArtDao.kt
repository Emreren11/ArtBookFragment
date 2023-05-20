package com.emre.navigationartbook.view.roomdb

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.emre.navigationartbook.view.model.Arts
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable

@Dao
interface ArtDao {

    @Query("select * from arts")
    fun getAll(): Flowable<List<Arts>>

    @Query("select * from arts where id = :id")
    fun getItemWithId(id: Int): Flowable<List<Arts>>

    @Insert
    fun insert(arts: Arts): Completable

}