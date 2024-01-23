package io.github.shams66789.contacts.roomdb.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import io.github.shams66789.contacts.roomdb.entity.Contact

@Dao
interface ContactDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun createContact(contact : Contact) : Long

    @Update
    fun updateContact(contact: Contact)

    @Query("SELECT * FROM CONTACT" )
    fun readContact() : LiveData<List<Contact>>

    @Query("SELECT * FROM CONTACT WHERE ID = :id1" )
    fun readContact(id1 : Int) : Contact

    @Delete
    fun deleteContact(contact: Contact)


}