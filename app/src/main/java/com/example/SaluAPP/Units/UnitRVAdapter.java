package com.example.SaluAPP.Units;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.SaluAPP.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UnitRVAdapter extends RecyclerView.Adapter<UnitRVAdapter.ViewHolder> {
    private ArrayList<UnitRVModal> unitRVModalArrayList;
    private Context context;
    private UnitClickInterface unitClickInterface;
    int lastPos = -1;

    public UnitRVAdapter(ArrayList<UnitRVModal> unitRVModalArrayList, Context context, UnitClickInterface unitClickInterface) {
        this.unitRVModalArrayList = unitRVModalArrayList;
        this.context = context;
        this.unitClickInterface = unitClickInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.subject_rv_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        UnitRVModal unitRVModal = unitRVModalArrayList.get(position);
        holder.unitTV.setText(unitRVModal.getUnitName());
        Picasso.get().load(unitRVModal.getUnitImg()).into(holder.unitIV);
        setAnimation(holder.itemView, position);
        holder.unitIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unitClickInterface.onUnitClick(position);
            }
        });
    }

    private void setAnimation(View itemView, int position) {
        if (position > lastPos) {
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
            itemView.setAnimation(animation);
            lastPos = position;
        }
    }

    @Override
    public int getItemCount() {
        return unitRVModalArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView unitIV;
        private TextView unitTV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            unitIV = itemView.findViewById(R.id.idIVSubject);
            unitTV = itemView.findViewById(R.id.idTVSubjectName);
        }
    }

    public interface UnitClickInterface {
        void onUnitClick(int position);
    }

      /* public void searchDataList(ArrayList<SubjectRVModal> searchList){
        subjectRVModalArrayList = searchList;
        notifyDataSetChanged();
    }*/

    public void searchDataList(ArrayList<UnitRVModal> searchList){
        unitRVModalArrayList = searchList;
        notifyDataSetChanged();
    }

}
