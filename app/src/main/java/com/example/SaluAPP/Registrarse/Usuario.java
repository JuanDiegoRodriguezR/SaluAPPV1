package com.example.SaluAPP.Registrarse;

public class Usuario {
    private String correo;
    private String nombre;
    private String telefono;

    public Usuario(String correo, String nombre, String telefono) {
        this.correo = correo;
        this.nombre = nombre;
        this.telefono = telefono;
    }

    // Getters y Setters
    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
}

