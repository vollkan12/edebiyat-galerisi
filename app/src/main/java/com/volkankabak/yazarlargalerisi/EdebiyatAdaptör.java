package com.volkankabak.yazarlargalerisi;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.volkankabak.yazarlargalerisi.databinding.RecyclerRowBinding;

import java.util.ArrayList;

public class EdebiyatAdaptör extends RecyclerView.Adapter<EdebiyatAdaptör.EdebiyatHolder> {

    ArrayList<Edebiyat> edebiyatArrayList;
    public EdebiyatAdaptör(ArrayList<Edebiyat>edebiyatArrayList){
        this.edebiyatArrayList = edebiyatArrayList;
    }

    @NonNull
    @Override
    public EdebiyatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerRowBinding recyclerRowBinding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.getContext()), parent,false);
        return new  EdebiyatHolder(recyclerRowBinding);

    }

    @Override
    public void onBindViewHolder(@NonNull EdebiyatHolder holder, int position) {

      holder.binding.recyclerViewTextView.setText(edebiyatArrayList.get(position).name);
      holder.itemView.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              Intent intent = new Intent(holder.itemView.getContext(),YazarlarGalerisi.class);
              intent.putExtra("info","old");
              intent.putExtra("edebiyatId",edebiyatArrayList.get(position).id);
              holder.itemView.getContext().startActivity(intent);
          }
      });

    }

    @Override
    public int getItemCount() {
        return edebiyatArrayList.size();
    }

    public class EdebiyatHolder extends RecyclerView.ViewHolder {
        private RecyclerRowBinding binding;

        public EdebiyatHolder(RecyclerRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
