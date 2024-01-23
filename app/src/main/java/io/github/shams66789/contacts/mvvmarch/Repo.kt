package io.github.shams66789.contacts.mvvmarch

import android.content.Context
import androidx.lifecycle.LiveData
import io.github.shams66789.contacts.roomdb.Database
import io.github.shams66789.contacts.roomdb.DbBuilder
import io.github.shams66789.contacts.roomdb.entity.Contact

class Repo(context: Context) {
    var database: Database? = null

    init {
        database = DbBuilder.getdb(context)
    }

    fun getData(): LiveData<List<Contact>>? {
        return  database?.ContactDao()?.readContact()
    }

    fun deleteData(contact: Contact) {
        database?.ContactDao()?.deleteContact(contact)
    }

    fun createData(contact: Contact): Long? {
        return database?.ContactDao()?.createContact(contact)
    }

    fun updateData(contact: Contact) {
        database?.ContactDao()?.updateContact(contact)
    }

}