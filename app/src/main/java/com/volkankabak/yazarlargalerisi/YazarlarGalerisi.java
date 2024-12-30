package com.volkankabak.yazarlargalerisi;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.snackbar.Snackbar;
import com.volkankabak.yazarlargalerisi.databinding.ActivityYazarlarGalerisiBinding;

import java.io.ByteArrayOutputStream;

public class YazarlarGalerisi extends AppCompatActivity {

    private ActivityYazarlarGalerisiBinding binding;
    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityResultLauncher<String> permissionLauncher;
    Bitmap selectedImage;
    SQLiteDatabase database;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityYazarlarGalerisiBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        registerLauncher();
        database = this.openOrCreateDatabase("Edebiyat",MODE_PRIVATE,null);
        Intent intent = getIntent();
        String info = intent.getStringExtra("info");

        if(info.equals("new")){
            //new Yazar
            binding.nameText.setText("");
            binding.nameText.setText("");
            binding.tarihText.setText("");
            binding.button.setVisibility(View.VISIBLE);
            binding.imageView.setImageResource(R.drawable.select);
        }else{
            int edebiyatId = intent.getIntExtra("edebiyatId",1);
            binding.button.setVisibility(View.INVISIBLE);

            try{
                Cursor cursor = database.rawQuery("SELECT *FROM edebiyat WHERE id = ? ", new String[]{String.valueOf(edebiyatId)});
                int edebiyatciNameIx = cursor.getColumnIndex("edebiyatciname");
                int eserNameIx = cursor.getColumnIndex("esername");
                int yearIx = cursor.getColumnIndex("year");
                int imageIx = cursor.getColumnIndex("image");

                while (cursor.moveToNext()){
                    binding.nameText.setText(cursor.getString(edebiyatciNameIx));
                    binding.EserText.setText(cursor.getString(eserNameIx));
                    binding.tarihText.setText(cursor.getString(yearIx));

                    byte[] bytes = cursor.getBlob(imageIx);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                    binding.imageView.setImageBitmap(bitmap);
                }
                cursor.close();

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    public void kaydet (View view) {

        String nameText =binding.nameText.getText().toString();
        String EserText =binding.EserText.getText().toString();
        String tarihText=binding.tarihText.getText().toString();

        Bitmap smallImage = makeSmallerImage(selectedImage,300);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        smallImage.compress(Bitmap.CompressFormat.PNG,50, outputStream);
        byte[] bytArray = outputStream.toByteArray();

        try{


          database.execSQL("CREATE TABLE IF NOT EXISTS edebiyat (id INTEGER PRİMARY KEY, edebiyatciname VARCHAR, esername VARCHAR, year VARCHAR, image BLOB )");

        String sqlString = "INSERT INTO edebiyat (edebiyatciname, esername, year, image) VALUES(?, ?, ?, ?)";
            SQLiteStatement sqLiteStatement = database.compileStatement(sqlString);
            sqLiteStatement.bindString(1,nameText);
            sqLiteStatement.bindString(2,EserText);
            sqLiteStatement.bindString(3,tarihText);
            sqLiteStatement.bindBlob(4,bytArray);
            sqLiteStatement.execute();

        }catch (Exception e){
            e.printStackTrace();
        }

        Intent intent = new Intent(YazarlarGalerisi.this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }
    public  Bitmap makeSmallerImage(Bitmap image,int maximumSize){
        int widht = image.getWidth();
        int hight = image.getHeight();


        float bitmapRatio = (float) widht / (float) hight;
        if(bitmapRatio > 1){
            //landscape image
            widht = maximumSize;
            hight = (int) (widht / bitmapRatio);
        }else{
            //portrait image
            hight = maximumSize;
            widht = (int) (hight * bitmapRatio);
        }

        return image.createScaledBitmap(image,widht,hight,true);
    }

    public void selectImage(View view){

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                Snackbar.make(view,"Galerinize erişmek için izninize ihtiyacımız var", Snackbar.LENGTH_INDEFINITE).setAction("İzin ver", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //izin isteme
                         permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                    }
                }).show();

            }else{
                //izin isteme
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);

            }

        }else{
            //Galeri
            Intent intenToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activityResultLauncher.launch(intenToGallery);
        }

    }
    private void registerLauncher(){
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
            if(result.getResultCode() == RESULT_OK){
                Intent intentFromResult = result.getData();
                if(intentFromResult !=null){
                  Uri imageData = intentFromResult.getData()  ;
                  //binding.imageView.setImageURI(imageData);

                    try{
                        if(Build.VERSION.SDK_INT >= 28){

                            ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(),imageData);
                            selectedImage=  ImageDecoder.decodeBitmap(source);
                            binding.imageView.setImageBitmap(selectedImage);
                        }else{
                            selectedImage = MediaStore.Images.Media.getBitmap(YazarlarGalerisi.this.getContentResolver(),imageData);
                            binding.imageView.setImageBitmap(selectedImage);
                        }

                    }catch (Exception e){
                       e.printStackTrace();
                    }
                }
            }
            }
        });
        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if(result){
                    // izin verildi
                    Intent intenToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                      activityResultLauncher.launch(intenToGallery);
                }else {
                    //izin verilmedi
                    Toast.makeText(YazarlarGalerisi.this,"İzin grekiyor!",Toast.LENGTH_LONG).show();
                }

            }
        });
    }


}