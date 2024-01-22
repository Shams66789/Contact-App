package io.github.shams66789.contacts.roomdb.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Contact(
    @PrimaryKey var id : Int? = null,
    var profile : Int? = null,
    var name : String,
    var phoneNo : String,
    var email : String? = null
)