package io.github.shams66789.contacts

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import io.github.shams66789.contacts.databinding.ActivityMainBinding
import io.github.shams66789.contacts.roomdb.DbBuilder
import io.github.shams66789.contacts.roomdb.entity.Contact

class MainActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    var contactList = ArrayList<Contact>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.floatingActionButton.setOnClickListener {
            startActivity(Intent(this@MainActivity, CreateContact::class.java))
        }

        DbBuilder.getdb(this)?.ContactDao()?.let {
            contactList.addAll(it.readContact())
        }

        binding.rv.layoutManager = LinearLayoutManager(this)
        binding.rv.adapter = ContactAdapter(contactList, this)
    }
}