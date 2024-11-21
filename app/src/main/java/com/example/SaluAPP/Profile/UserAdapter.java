package com.example.SaluAPP.Profile;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.SaluAPP.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    Context context;
    private ArrayList<User> userArrayList;
    private FirebaseFirestore db;
    private String currentUserId;
    private String currentUserRole;

    public UserAdapter(Context context, ArrayList<User> userArrayList, String currentUserId, String currentUserRole) {
        this.context = context;
        this.userArrayList = userArrayList;
        this.db = FirebaseFirestore.getInstance();
        this.currentUserId = currentUserId;
        this.currentUserRole = currentUserRole;
    }

    @NonNull
    @Override
    public UserAdapter.UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.UserViewHolder holder, int position) {
        User user = userArrayList.get(position);

        // Verificar si los valores son nulos y asignar valores por defecto
        holder.Correo.setText(user.getCorreo() != null ? user.getCorreo() : "Correo no disponible");
        holder.Name.setText(user.getNombre_Apellido() != null ? user.getNombre_Apellido() : "Nombre no disponible");

        // Validar si el rol es nulo o vacío antes de asignarlo
        String role = user.getIdRoles() != null && !user.getIdRoles().isEmpty() ? user.getIdRoles() : "0"; // Valor por defecto
        switch (role) {
            case "0":
                holder.IdRoles.setText("Usuario");
                break;
            case "1":
                holder.IdRoles.setText("Profesional");
                break;
            case "2":
                holder.IdRoles.setText("Administrador");
                break;
            case "3":
                holder.IdRoles.setText("Moderador");
                break;
            default:
                holder.IdRoles.setText("Desconocido");
        }

        // Verificar el estado antes de mostrarlo
        String status = user.getStatus() != null ? user.getStatus() : "0";  // Valor por defecto
        switch (status) {
            case "0":
                holder.Status.setText("Activo");
                break;
            case "1":
                holder.Status.setText("Baneado");
                break;
            default:
                holder.Status.setText("Desconocido");
        }

        holder.Telefono.setText(user.getTelefono() != null ? user.getTelefono() : "Teléfono no disponible");

        // Verificar si el usuario tiene rol 3 (Moderador) y deshabilitar la edición de rol
        if ("3".equals(currentUserRole)) {
            // Si el rol es 3, deshabilitar el botón de editar
            holder.btnEditUser.setEnabled(false);
            holder.btnEditUser.setVisibility(View.GONE);  // También se puede ocultar el botón
        } else {
            // Si el rol no es 3, permitir la edición de rol
            holder.btnEditUser.setEnabled(true);
            holder.btnEditUser.setVisibility(View.VISIBLE);
        }

        // Botón de baneo
        holder.btnBanUser.setOnClickListener(v -> {
            if (user.getUid().equals(currentUserId)) {
                Toast.makeText(context, "No puedes banearte a ti mismo", Toast.LENGTH_SHORT).show();
                return;
            }

            // Verificar si el rol del usuario autenticado es 3 (Moderador) y el rol del usuario a editar es 2 (Administrador)
            if ("3".equals(currentUserRole) && "2".equals(user.getIdRoles())) {
                Toast.makeText(context, "No puedes banear a un Administrador", Toast.LENGTH_SHORT).show();
                return;
            }

            String message = user.getStatus().equals("1")
                    ? "¿Quieres desbanear a " + user.getNombre_Apellido() + "?"
                    : "¿Banear a " + user.getNombre_Apellido() + "?";

            new AlertDialog.Builder(context)
                    .setTitle("Confirmar")
                    .setMessage(message)
                    .setPositiveButton("Sí", (dialog, which) -> {
                        String newStatus = user.getStatus().equals("0") ? "1" : "0";

                        db.collection("users").document(user.getUid())
                                .update("Status", newStatus)
                                .addOnSuccessListener(aVoid -> {
                                    user.setStatus(newStatus);
                                    notifyItemChanged(position);
                                    Toast.makeText(context, "Estado actualizado con éxito", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(context, "Error al actualizar el estado: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    })
                    .setNegativeButton("No", null)
                    .show();
        });

        // Configuración del botón para editar el rol (sin cambios)
        holder.btnEditUser.setOnClickListener(v -> {
            // No permitir que un usuario con rol 3 (Moderador) edite roles
            if ("3".equals(currentUserRole)) {
                Toast.makeText(context, "No puedes editar roles como Moderador", Toast.LENGTH_SHORT).show();
                return;
            }

            if (user.getUid().equals(currentUserId)) {
                Toast.makeText(context, "No puedes modificar tu propio rol", Toast.LENGTH_SHORT).show();
                return;
            }

            Spinner roleSpinner = new Spinner(context);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, new String[]{"Usuario", "Profesional", "Administrador", "Moderador"});
            roleSpinner.setAdapter(adapter);
            int currentRole = Integer.parseInt(user.getIdRoles());
            roleSpinner.setSelection(currentRole);

            new AlertDialog.Builder(context)
                    .setTitle("Editar Rol")
                    .setView(roleSpinner)
                    .setPositiveButton("Guardar", (dialog, which) -> {
                        int selectedRole = roleSpinner.getSelectedItemPosition();
                        db.collection("users").document(user.getUid())
                                .update("IdRoles", String.valueOf(selectedRole))
                                .addOnSuccessListener(aVoid -> {
                                    user.setIdRoles(String.valueOf(selectedRole));
                                    notifyItemChanged(position);
                                    Toast.makeText(context, "Rol actualizado a " + getRoleName(selectedRole), Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(context, "Error al actualizar el rol: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });
    }


    @Override
    public int getItemCount() {
        return userArrayList.size();
    }

    private String getRoleName(int role) {
        switch (role) {
            case 0: return "Usuario";
            case 1: return "Profesional";
            case 2: return "Administrador";
            case 3: return "Moderador";
            default: return "Desconocido";
        }
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView Name, Correo, IdRoles, Telefono, Status;
        ImageButton btnBanUser, btnEditUser;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            Correo = itemView.findViewById(R.id.tvCorreo);
            IdRoles = itemView.findViewById(R.id.tvRol);
            Name = itemView.findViewById(R.id.tvNombre);
            Status = itemView.findViewById(R.id.tvStatus);
            Telefono = itemView.findViewById(R.id.tvTelefono);
            btnBanUser = itemView.findViewById(R.id.btnBanUser);
            btnEditUser = itemView.findViewById(R.id.btnEditUser);
        }
    }
}