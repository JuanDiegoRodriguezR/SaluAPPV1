package com.example.SaluAPP.Units;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.example.SaluAPP.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddUnitsActivity extends AppCompatActivity {
    private Button addUnitBtn;
    private TextInputEditText unitNameEdt, unitDescEdt, unitImgEdt, unitVideoEdt, unitRecommendedEdt;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private ProgressBar loadingPB;
    private String unitId;
    private String subjectId, courseId;
    private String mateID;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_units);
        // Inicializando todas nuestras variables
        addUnitBtn = findViewById(R.id.BtnAddUnit);
        unitNameEdt = findViewById(R.id.idEdtUnitName);
        unitDescEdt = findViewById(R.id.idEdtUnitDescription);
        unitImgEdt = findViewById(R.id.idEdtUnitImageLink);
        unitRecommendedEdt = findViewById(R.id.idEdtUnitRecommended);
        unitVideoEdt = findViewById(R.id.idEdtVILUnitLink);
        loadingPB = findViewById(R.id.idPBUnitLoading);
        mateID = getIntent().getStringExtra("mateID");
        firebaseDatabase = FirebaseDatabase.getInstance();
        subjectId = getIntent().getStringExtra("subjectId");
        courseId = getIntent().getStringExtra("courseId");
        // En la siguiente línea estamos creando nuestra referencia de base de datos.
        databaseReference = firebaseDatabase.getReference("Materias").child(mateID).child("Subjects").child(subjectId).child("Courses").child(courseId).child("Units");
        //databaseReference = firebaseDatabase.getReference("Subjects").child(subjectId).child("Courses").child(courseId).child("Units");


        // Agregando un listener de clic para nuestro botón de añadir unidad.
        addUnitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Obtener datos de nuestros campos de texto.
                String unitName = unitNameEdt.getText().toString().trim();
                String unitDesc = unitDescEdt.getText().toString().trim();
                String unitImg = unitImgEdt.getText().toString().trim();
                String unitRcmd = unitRecommendedEdt.getText().toString().trim();
                String unitVid = unitVideoEdt.getText().toString().trim();

                if (TextUtils.isEmpty(unitName)){
                    unitNameEdt.setError("Ingrese el nombre de la unidad");
                    return;
                }

                if (TextUtils.isEmpty(unitImg)){
                    unitImgEdt.setError("Ingrese URL de la imagen de la unidad");
                    return;
                }

                if (TextUtils.isEmpty(unitDesc)){
                    unitDescEdt.setError("Ingrese la descripcion de la unidad");
                    return;
                }

                if (TextUtils.isEmpty(unitRcmd)){
                    unitRecommendedEdt.setError("Ingrese una recomendacion para la unidad");
                    return;
                }

                if (TextUtils.isEmpty(unitVid)){
                    unitVideoEdt.setError("Ingrese el id del video de Dailymotion");
                    return;
                }

                if (unitVid.length() < 7 ){
                    unitVideoEdt.setError("El id de video no puede ser menor a 7 caracteres");
                    return;
                }

                if (unitVid.length() > 20 ){
                    unitVideoEdt.setError("El id de video no puede ser mayor a 20 caracteres");
                    return;
                }



                if (unitName.isEmpty() || unitDesc.isEmpty() || unitImg.isEmpty() || unitRcmd.isEmpty() || unitVid.isEmpty()) {
                    // Mostrar una alerta si algún campo está vacío.
                    Toast.makeText(AddUnitsActivity.this, "Por favor complete todos los campos.", Toast.LENGTH_SHORT).show();
                    loadingPB.setVisibility(View.GONE);
                } else {
                    if (android.util.Patterns.WEB_URL.matcher(unitImg).matches()) {
                        loadingPB.setVisibility(View.VISIBLE);
                        // La cadena es una URL válida
                        // Continuar con la lógica de agregar a la base de datos si todos los campos están completos.
                        unitId = databaseReference.push().getKey(); // Generar un ID único para la unidad
                        if (unitId != null) {
                            UnitRVModal unitRVModal = new UnitRVModal(unitId, unitName, unitDesc, unitImg, unitRcmd, unitVid);
                            databaseReference.child(unitId).setValue(unitRVModal)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            loadingPB.setVisibility(View.GONE);
                                            Toast.makeText(AddUnitsActivity.this, "¡Unidad añadida!", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(AddUnitsActivity.this, UnitMain.class));
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(AddUnitsActivity.this, "No se pudo agregar la unidad.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            Toast.makeText(AddUnitsActivity.this, "No se pudo obtener un ID para la unidad.", Toast.LENGTH_SHORT).show();
                            loadingPB.setVisibility(View.GONE);
                        }
                    }else {
                        // La cadena no es una URL válida
                        Toast.makeText(AddUnitsActivity.this, "Por favor verifica que las url sean validas.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
