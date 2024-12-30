package com.volkankabak.yazarlargalerisi;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.volkankabak.yazarlargalerisi.databinding.ActivityMainBinding;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    ArrayList<Edebiyat> edebiyatArrayList;
    EdebiyatAdaptör edebiyatAdaptör;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityMainBinding.inflate(getLayoutInflater());
        View view =binding.getRoot();
        setContentView(view);

        edebiyatArrayList = new ArrayList<>();

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        edebiyatAdaptör = new EdebiyatAdaptör(edebiyatArrayList);
        binding.recyclerView.setAdapter(edebiyatAdaptör);

        getData();
    }

    private  void getData(){
        try{
            SQLiteDatabase sqLiteDatabase = this.openOrCreateDatabase("Edebiyat",MODE_PRIVATE,null);

            Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM edebiyat",null);
            int nameIx = cursor.getColumnIndex("edebiyatciname");
            int idIx = cursor.getColumnIndex("id");

            while (cursor.moveToNext()){
                String name  = cursor.getString(nameIx);
                int id = cursor.getInt(idIx);
                Edebiyat edebiyat =new Edebiyat(name,id);
                edebiyatArrayList.add(edebiyat);
            }

            edebiyatAdaptör.notifyDataSetChanged();
            cursor.close();

        }catch (Exception e){
         e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater= getMenuInflater();
        menuInflater.inflate(R.menu.edebiyat_menu,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
         if(item.getItemId() ==R.id.sanatci_ekle){
             Intent intent = new Intent(this,YazarlarGalerisi.class);
             intent.putExtra("info","new");
         startActivity(intent);
         }

        return super.onOptionsItemSelected(item);
    }
}