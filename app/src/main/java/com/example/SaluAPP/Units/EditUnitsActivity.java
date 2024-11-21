package com.example.SaluAPP.Units;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.SaluAPP.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class EditUnitsActivity extends AppCompatActivity {

    private TextInputEditText UnitNameEdt, UnitDescEdt, UnitImgEdt, UnitRcmdEdt, UnitVidEdt;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    UnitRVModal UnitRVModal;
    private ProgressBar loadingPB;
    private String UnitID;
    private String subjectId, courseId;

    private String mateID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_units);
        Button editUnitBtn = findViewById(R.id.idBtnAddUnit);
        UnitNameEdt = findViewById(R.id.idEdtUnitName);
        UnitDescEdt = findViewById(R.id.idEdtUnitDescription);
        mateID = getIntent().getStringExtra("mateID");
        UnitImgEdt = findViewById(R.id.idEdtUnitImageLink);
        UnitRcmdEdt = findViewById(R.id.idEdtUnitRecommended);
        UnitVidEdt = findViewById(R.id.idEdtVILUnitLink);
        loadingPB = findViewById(R.id.idPBUnitLoading);
        firebaseDatabase = FirebaseDatabase.getInstance();
        UnitRVModal = getIntent().getParcelableExtra("unit");
        subjectId = getIntent().getStringExtra("subjectId");
        courseId = getIntent().getStringExtra("courseId");
        Button deleteUnitBtn = findViewById(R.id.BtnDeleteUnit);

        if (UnitRVModal != null) {
            UnitNameEdt.setText(UnitRVModal.getUnitName());
            UnitImgEdt.setText(UnitRVModal.getUnitImg());
            UnitDescEdt.setText(UnitRVModal.getUnitDescription());
            UnitRcmdEdt.setText(UnitRVModal.getUnitRcmd());
            UnitVidEdt.setText(UnitRVModal.getUnitVid());
            UnitID = UnitRVModal.getUnitId();
        }
        databaseReference = firebaseDatabase.getReference("Materias").child(mateID).child("Subjects").child(subjectId).child("Courses").child(courseId).child("Units");
       // databaseReference = firebaseDatabase.getReference("Subjects").child(subjectId).child("Courses").child(courseId).child("Units");
        editUnitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingPB.setVisibility(View.VISIBLE);

                String UnitName = UnitNameEdt.getText().toString().trim();
                String UnitDesc = UnitDescEdt.getText().toString().trim();
                String UnitImg = UnitImgEdt.getText().toString().trim();
                String UnitRcmd = UnitRcmdEdt.getText().toString().trim();
                String UnitVid = UnitVidEdt.getText().toString().trim();

                if (TextUtils.isEmpty(UnitName)){
                    UnitNameEdt.setError("Ingrese el nombre de la unidad para editar");
                    return;
                }

                if (TextUtils.isEmpty(UnitImg)){
                    UnitImgEdt.setError("Ingrese la URL de la imagen para editar");
                    return;
                }

                if (TextUtils.isEmpty(UnitDesc)){
                    UnitDescEdt.setError("Ingrese descripcion de la unidad para editar");
                    return;
                }

                if (TextUtils.isEmpty(UnitRcmd)){
                    UnitRcmdEdt.setError("Ingrese una recomendacion para editar");
                    return;
                }

                if (TextUtils.isEmpty(UnitVid) ){
                    UnitVidEdt.setError("Ingrese el id del video en Dailymotion para editar");
                    return;
                }

                if (UnitVid.length() < 7 ){
                    UnitVidEdt.setError("El id de video no puede ser menor a 7 caracteres");
                    return;
                }

                if (UnitVid.length() > 20 ){
                    UnitVidEdt.setError("El id de video no puede ser mayor a 20 caracteres");
                    return;
                }

                if (UnitName.isEmpty() || UnitDesc.isEmpty() || UnitImg.isEmpty() || UnitRcmd.isEmpty() || UnitVid.isEmpty()) {
                    Toast.makeText(EditUnitsActivity.this, "Por favor complete todos los campos.", Toast.LENGTH_SHORT).show();
                    loadingPB.setVisibility(View.GONE);
                } else {
                    Map<String, Object> map = new HashMap<>();
                    if (!UnitName.equals(UnitRVModal.getUnitName())) {
                        map.put("unitName", UnitName);
                    }
                    if (!UnitDesc.equals(UnitRVModal.getUnitDescription())) {
                        map.put("unitDescription", UnitDesc);
                    }
                    if (!UnitImg.equals(UnitRVModal.getUnitImg())) {
                        map.put("unitImg", UnitImg);
                    }
                    if (!UnitRcmd.equals(UnitRVModal.getUnitRcmd())) {
                        map.put("unitRcmd", UnitRcmd);
                    }
                    if (!UnitVid.equals(UnitRVModal.getUnitVid())) {
                        map.put("unitVid", UnitVid);
                    }
                    map.put("unitId", UnitID);

                    // Verifica si UnitRVModal no es nulo antes de actualizar los datos
                    if (UnitRVModal != null) {
                        // Actualiza los datos del Unidad en la base de datos
                        databaseReference.child(UnitID).updateChildren(map)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        loadingPB.setVisibility(View.GONE);
                                        Toast.makeText(EditUnitsActivity.this, "Unidad actualizada.", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(EditUnitsActivity.this, UnitMain.class));
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(EditUnitsActivity.this, "Fallo al actualizar la unidad.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Toast.makeText(EditUnitsActivity.this, "Fallo al cargar los datos de la unidad.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


        deleteUnitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UnitRVModal != null) {
                    deleteUnit(UnitID);
                } else {
                    Toast.makeText(EditUnitsActivity.this, "No se pudo eliminar la unidad. Unidad no encontrada.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void deleteUnit(String UnitId) {
        DatabaseReference unitRef = databaseReference.child(UnitId);
        unitRef.removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(EditUnitsActivity.this, "Unidad eliminada.", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(EditUnitsActivity.this, UnitMain.class));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditUnitsActivity.this, "No se pudo eliminar la unidad.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
