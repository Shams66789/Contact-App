package io.github.shams66789.contacts.mvvmarch

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import io.github.shams66789.contacts.roomdb.entity.Contact

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {
    var repo : Repo
    var data : LiveData<List<Contact>>
    init {
        repo= Repo(application)
        data = repo.getData()!!
    }

    fun deleteData(contact: Contact) {
        repo.deleteData(contact)
    }
}
