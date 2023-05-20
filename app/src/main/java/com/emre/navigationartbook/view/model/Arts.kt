package com.emre.navigationartbook.view.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Arts(

    @ColumnInfo(name = "name")
    var name: String,

    @ColumnInfo(name = "artist")
    var artist: String,

    @ColumnInfo(name = "year")
    var year: String,

    @ColumnInfo(name = "image")
    var image: ByteArray

    ) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}