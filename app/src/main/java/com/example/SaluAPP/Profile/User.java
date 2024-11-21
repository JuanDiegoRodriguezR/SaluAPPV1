package com.example.SaluAPP.Profile;

public class User {

    private String UID;
    private String Correo;
    private String IdRoles;
    private String Nombre_Apellido;
    private String Status;
    private String Telefono;

    public User() {
        // Constructor vacío necesario para Firestore
    }

    public User(String UID, String Correo, String IdRoles, String Nombre_Apellido, String Status, String Telefono) {
        this.UID = UID;
        this.Correo = Correo;
        this.IdRoles = IdRoles;
        this.Nombre_Apellido = Nombre_Apellido;
        this.Status = Status;
        this.Telefono = Telefono;
    }

    public String getUid() {
        return UID;
    }

    public String getCorreo() {
        return Correo;
    }

    public String getIdRoles() {
        return IdRoles;
    }

    public String getNombre_Apellido() {
        return Nombre_Apellido;
    }

    public String getStatus() {
        return Status;
    }

    public String getTelefono() {
        return Telefono;
    }

    public void setStatus(String status) {
        this.Status = status;
    }

    public void setIdRoles(String idRoles) {
        this.IdRoles = idRoles;
    }
}