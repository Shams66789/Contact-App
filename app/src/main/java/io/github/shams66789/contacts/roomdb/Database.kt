package io.github.shams66789.contacts.roomdb

import androidx.room.Database
import androidx.room.RoomDatabase
import io.github.shams66789.contacts.roomdb.dao.ContactDao
import io.github.shams66789.contacts.roomdb.entity.Contact

@Database(entities = [Contact::class], version = 3, exportSchema = false)
abstract class Database : RoomDatabase() {
    abstract fun ContactDao(): ContactDao

}