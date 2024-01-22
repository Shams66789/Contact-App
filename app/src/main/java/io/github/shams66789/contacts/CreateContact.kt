package io.github.shams66789.contacts

import android.app.Activity
import android.app.Dialog
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import com.github.dhaval2404.imagepicker.ImagePicker
import io.github.shams66789.contacts.databinding.ActivityCreateContactBinding
import io.github.shams66789.contacts.roomdb.DbBuilder
import io.github.shams66789.contacts.roomdb.entity.Contact

class CreateContact : AppCompatActivity() {
    private val binding by lazy {
        ActivityCreateContactBinding.inflate(layoutInflater)
    }
    var contact = Contact()
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

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.imageView.setOnLongClickListener {
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.image_dialog)

            val image = dialog.findViewById<ImageView>(R.id.imageView2)
            val imageObject = binding.imageView.drawable
            image.setImageDrawable(imageObject)
            val lp = WindowManager.LayoutParams()
            lp.width = WindowManager.LayoutParams.WRAP_CONTENT
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT
            lp.blurBehindRadius = 100
            lp.flags = WindowManager.LayoutParams.FLAG_BLUR_BEHIND
            dialog.window?.attributes = lp
            dialog.show()
            true
        }

        binding.button.setOnClickListener {
            contact.name =  binding.name.editText?.text.toString()
            contact.phoneNo =  binding.phone.editText?.text.toString()
            contact.email =  binding.email.editText?.text.toString()

            DbBuilder.getdb(this)?.ContactDao()?.createContact(contact)
        }

        binding.imageView.setOnClickListener {
            ImagePicker.with(this)
                .createIntent { intent ->
                    startForProfileImageResult.launch(intent)
                }
        }


    }
}