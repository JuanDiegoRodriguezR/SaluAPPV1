package com.example.SaluAPP.Courses;


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

public class CourseRVAdapter extends RecyclerView.Adapter<CourseRVAdapter.ViewHolder> {
    // creando variables para nuestra lista, contexto, interfaz y posición.
    private ArrayList<CourseRVModal> courseRVModalArrayList;
    private Context context;
    private CourseClickInterface courseClickInterface;
    int lastPos = -1;

    // creando un constructor.
    public CourseRVAdapter(ArrayList<CourseRVModal> courseRVModalArrayList, Context context, CourseClickInterface courseClickInterface) {
        this.courseRVModalArrayList = courseRVModalArrayList;
        this.context = context;
        this.courseClickInterface = courseClickInterface;
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
        CourseRVModal courseRVModal = courseRVModalArrayList.get(position);
        holder.courseTV.setText(courseRVModal.getCourseName());
        Picasso.get().load(courseRVModal.getCourseImg()).into(holder.courseIV);
        // añadiendo animación al elemento de vista de reciclaje en la línea de abajo.
        setAnimation(holder.itemView, position);
        holder.courseIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                courseClickInterface.onCourseClick(position);
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

    @Override
    public int getItemCount() {
        return courseRVModalArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // creando variable para nuestra imagen y texto en la vista de reciclaje en la línea de abajo.
        private ImageView courseIV;
        private TextView courseTV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // inicializando todas nuestras variables en la línea de abajo.
            courseIV = itemView.findViewById(R.id.idIVSubject);
            courseTV = itemView.findViewById(R.id.idTVSubjectName);
        }
    }

    // creando una interfaz para el clic
    public interface CourseClickInterface {
        void onCourseClick(int position);
    }

    public void searchDataList(ArrayList<CourseRVModal> searchList){
        courseRVModalArrayList = searchList;
        notifyDataSetChanged();
    }

}
