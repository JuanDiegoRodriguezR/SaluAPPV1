package com.example.SaluAPP.Subject;


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

public class SubjectRVAdapter extends RecyclerView.Adapter<SubjectRVAdapter.ViewHolder> {
    // creando variables para nuestra lista, contexto, interfaz y posición.
    private ArrayList<SubjectRVModal> subjectRVModalArrayList;
    private Context context;
    private SubjectClickInterface subjectClickInterface;
    int lastPos = -1;

    // creando un constructor.
    public SubjectRVAdapter(ArrayList<SubjectRVModal> subjectRVModalArrayList, Context context, SubjectClickInterface subjectClickInterface) {
        this.subjectRVModalArrayList = subjectRVModalArrayList;
        this.context = context;
        this.subjectClickInterface = subjectClickInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // inflando nuestro archivo de diseño en la línea de abajo.
        View view = LayoutInflater.from(context).inflate(R.layout.subject_rv_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        // estableciendo datos en nuestro elemento de vista de reciclaje en la línea de abajo.
        SubjectRVModal subjectRVModal = subjectRVModalArrayList.get(position);
        holder.subjectTV.setText(subjectRVModal.getSubjectName());
        Picasso.get().load(subjectRVModal.getSubjectImg()).into(holder.subjectIV);
        // añadiendo animación al elemento de vista de reciclaje en la línea de abajo.
        setAnimation(holder.itemView, position);
        holder.subjectIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subjectClickInterface.onSubjectClick(position);
            }
        });
    }

    private void setAnimation(View itemView, int position) {
        if (position > lastPos) {
            // en la línea de abajo estamos estableciendo la animación.
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
            itemView.setAnimation(animation);
            lastPos = position;
        }
    }

    public void searchDataList(ArrayList<SubjectRVModal> searchList){
        subjectRVModalArrayList = searchList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return subjectRVModalArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // creando variable para nuestra imagen y texto en la vista de reciclaje en la línea de abajo.
        private ImageView subjectIV;
        private TextView subjectTV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // inicializando todas nuestras variables en la línea de abajo.
            subjectIV = itemView.findViewById(R.id.idIVSubject);
            subjectTV = itemView.findViewById(R.id.idTVSubjectName);
        }
    }

    // creando una interfaz para el clic
    public interface SubjectClickInterface {
        void onSubjectClick(int position);
    }

}
