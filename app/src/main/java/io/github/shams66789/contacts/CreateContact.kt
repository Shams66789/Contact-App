package io.github.shams66789.contacts

import android.app.Activity
import android.app.Dialog
import android.graphics.BitmapFactory
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import com.github.dhaval2404.imagepicker.ImagePicker
import io.github.shams66789.contacts.databinding.ActivityCreateContactBinding
import io.github.shams66789.contacts.mvvmarch.CreateContactViewModel
import io.github.shams66789.contacts.roomdb.DbBuilder
import io.github.shams66789.contacts.roomdb.entity.Contact

class CreateContact : AppCompatActivity() {
    private val binding by lazy {
        ActivityCreateContactBinding.inflate(layoutInflater)
    }
    lateinit var viewModel : CreateContactViewModel
    var contact = Contact()
    var flag = -1

    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data

            if (resultCode == Activity.RESULT_OK) {
                //Image Uri will not be null for RESULT_OK
                val fileUri = data?.data!!

                binding.imageView.setImageURI(fileUri)

                val imageBytes = contentResolver.openInputStream(fileUri)?.readBytes()
                contact.profile = imageBytes
            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(CreateContactViewModel::class.java)

        if (intent.hasExtra("FLAG", )) {
            flag = intent.getIntExtra("FLAG", -1)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                contact = intent.getSerializableExtra("DATA", Contact::class.java)!!
            } else {
                contact = intent.getSerializableExtra("DATA") as Contact
            }
            binding.button.text = "Update Contact"
            val imageByte = contact.profile
            if (imageByte != null) {
                val image = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.size)
                binding.imageView.setImageBitmap(image)
            }
            binding.name.editText?.setText(contact.name)
            binding.phone.editText?.setText(contact.phoneNo)
            binding.email.editText?.setText(contact.email)
        }

        binding.imageView.setOnLongClickListener {
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.image_dialog)

            val image = dialog.findViewById<ImageView>(R.id.imageView2)
            val imageObject = binding.imageView.drawable
            image.setImageDrawable(imageObject)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val lp = WindowManager.LayoutParams()
                lp.width = WindowManager.LayoutParams.WRAP_CONTENT
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT
                lp.blurBehindRadius = 100
                lp.flags = WindowManager.LayoutParams.FLAG_BLUR_BEHIND
                dialog.window?.attributes = lp
            }
            dialog.show()
            true
        }

        binding.button.setOnClickListener {
            contact.name =  binding.name.editText?.text.toString()
            contact.phoneNo =  binding.phone.editText?.text.toString()
            contact.email =  binding.email.editText?.text.toString()

            if (flag == 1) {
                viewModel.updateData(contact) {
                    if (it != null) {
                        if (it > 0) {
                            Toast.makeText(this@CreateContact, "Contact Updated", Toast.LENGTH_SHORT)
                                .show()
                            finish()
                        }
                    }
                }
            } else{
                viewModel.storeData(contact) {
                    if (it != null) {
                        if (it > 0) {
                            Toast.makeText(this@CreateContact, "Contact Created", Toast.LENGTH_SHORT)
                                .show()
                            finish()
                        }
                    }
                }
            }
        }

        binding.imageView.setOnClickListener {
            ImagePicker.with(this)
                .compress(1024)
                .maxResultSize(1080, 1080)
                .createIntent { intent ->
                    startForProfileImageResult.launch(intent)
                }
        }
    }
}