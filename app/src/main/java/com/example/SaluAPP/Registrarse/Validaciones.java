package com.example.SaluAPP.Registrarse;

import android.text.TextUtils;
import android.widget.CheckBox;
import android.widget.EditText;

public class Validaciones {

    public static boolean validarEmail(String email, EditText campo) {
        if (TextUtils.isEmpty(email)) {
            campo.setError("Su correo es requerido");
            return false;
        }
        return true;
    }

    public static boolean validarContraseña(String password, EditText campo) {
        if (TextUtils.isEmpty(password)) {
            campo.setError("Ingrese una contraseña");
            return false;
        }
        if (password.length() < 6) {
            campo.setError("La contraseña debe tener mas de 6 caracteres");
            return false;
        }
        return true;
    }

    public static boolean validarTelefono(String telefono, EditText campo) {
        if (TextUtils.isEmpty(telefono)) {
            campo.setError("Su telefono es requerido");
            return false;
        }
        if (!telefono.matches("[0-9]+")) {
            campo.setError("El registro debe ser numerico");
            return false;
        }
        if (telefono.length() != 10) {
            campo.setError("El telefono debe tener exactamente 10 números");
            return false;
        }
        return true;
    }

    public static boolean validarTerminos(CheckBox terminos) {
        if (!terminos.isChecked()) {
            terminos.setError("Acepte términos y condiciones");
            return false;
        }
        return true;
    }
}
