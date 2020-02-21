package com.example.databasepractice

import android.app.Activity
import android.app.Instrumentation
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.provider.ContactsContract
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.io.ByteArrayOutputStream
import java.io.FileDescriptor
import java.io.IOException
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    private val dbHelper = DbHelper(this)

    companion object{
        const val CHOOSE_PHOTO = 110
    }

    private var photoId = 0L
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //写真追加ボタンのクリック処理
        addPhotoButton.setOnClickListener{
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "image/*"

            startActivityForResult(Intent.createChooser(intent,"写真を選択"),CHOOSE_PHOTO)
        }

        //写真削除ボタンのクリック処理
        deletePhotoButton.setOnClickListener{
            val db = dbHelper.writableDatabase
            db.delete(PhotoTable.TABLE_NAME,"_ID = $photoId",null) //レコード削除

            imageView.setImageResource(R.drawable.art)

            Toast.makeText(this,"削除しました",Toast.LENGTH_LONG).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == CHOOSE_PHOTO
            && resultCode == Activity.RESULT_OK
            && data != null){
            val bitmap = getBitmapFromUri(data.data)
            bitmap?:return

            imageView.setImageBitmap(bitmap)

            val values = getContentValues(getBinaryFromBitmap(bitmap))
            val db = dbHelper.writableDatabase
            photoId = db.insert(PhotoTable.TABLE_NAME, null, values) //レコード追加

            Toast.makeText(this,"登録しました",Toast.LENGTH_LONG).show()
        }
    }

    //Bitmapを取得
    //@param 画像Uri
    //@return Bitmap
    private fun getBitmapFromUri(uri: Uri?): Bitmap? {
        uri?:return null

        val parcelFileDescriptor: ParcelFileDescriptor? =
            this.contentResolver.openFileDescriptor(uri, "r")
        parcelFileDescriptor?:return null

        val fileDescriptor: FileDescriptor = parcelFileDescriptor.fileDescriptor
        val image: Bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor)
        parcelFileDescriptor.close()
        return image
    }

    //Binaryを取得
    //@param Bitmap
    //@return Binary
    private fun getBinaryFromBitmap(bitmap:Bitmap):ByteArray{
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        return byteArrayOutputStream.toByteArray()
    }

    //値セットを取得
    //@param URI
    private fun getContentValues(binary:ByteArray): ContentValues {
        return ContentValues().apply {
            put("${PhotoTable.COLUMN_NAME_BITMAP}",binary)
        }
    }
}
