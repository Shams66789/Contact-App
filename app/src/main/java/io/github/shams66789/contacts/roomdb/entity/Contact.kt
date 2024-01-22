package io.github.shams66789.contacts.roomdb.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Contact(
    @PrimaryKey var id : Int? = null,
    var profile : ByteArray? = null,
    var name : String? = null,
    var phoneNo : String? = null,
    var email : String? = null
)