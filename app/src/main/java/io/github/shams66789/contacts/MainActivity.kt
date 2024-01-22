package io.github.shams66789.contacts

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import io.github.shams66789.contacts.databinding.ActivityMainBinding
import io.github.shams66789.contacts.roomdb.Database
import io.github.shams66789.contacts.roomdb.DbBuilder
import io.github.shams66789.contacts.roomdb.entity.Contact

class MainActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

//        DbBuilder.getdb(this)?.ContactDao()?.createContact(Contact(name = "Shams",
//            phoneNo = "+918617", email = "rai@dskh"))
    }
}