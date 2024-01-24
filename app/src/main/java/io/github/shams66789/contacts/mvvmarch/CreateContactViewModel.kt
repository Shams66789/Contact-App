package io.github.shams66789.contacts.mvvmarch

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import io.github.shams66789.contacts.roomdb.entity.Contact

class CreateContactViewModel(application: Application) : AndroidViewModel(application) {
    var repo: Repo
    init {

        repo = Repo(application)

    }
    fun storeData(contact: Contact, function:(i : Long?)-> Unit){
        val i = repo.createData(contact)
        function(i)
    }

    fun updateData(contact: Contact, function: (i: Int?) -> Unit){
        function(repo.updateData(contact))
    }
}