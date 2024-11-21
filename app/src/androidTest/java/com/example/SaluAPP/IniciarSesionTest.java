package com.example.SaluAPP;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class IniciarSesionTest {

    @Rule
    public ActivityTestRule<MainActivity> activityRule =
            new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setUp() {
        // Lanza la actividad antes de cada prueba
        activityRule.launchActivity(new Intent());
    }

    @Test
    public void testCamposVacios() {
        // Presiona el botón sin ingresar datos
        onView(withId(R.id.enviarButton)).perform(click());

        // Verifica que aparezca el error en el campo de correo
        onView(withId(R.id.CorreoInicio)).check(matches(withText("Ingrese su email")));
    }

    @Test
    public void testInicioSesionExitoso() {
        // Ingresa un correo válido
        onView(withId(R.id.CorreoInicio))
                .perform(typeText("usuario@gmail.com"), closeSoftKeyboard());

        // Ingresa una contraseña válida
        onView(withId(R.id.ContraseñaInicio))
                .perform(typeText("123456"), closeSoftKeyboard());

        // Haz clic en el botón de enviar
        onView(withId(R.id.enviarButton)).perform(click());

        // Verifica si se muestra la siguiente actividad o mensaje de éxito
        onView(withText("Usuario entró con éxito")).check(matches(isDisplayed()));
    }

    @Test
    public void testContraseñaCorta() {
        // Ingresa un correo válido
        onView(withId(R.id.CorreoInicio))
                .perform(typeText("usuario@gmail.com"), closeSoftKeyboard());

        // Ingresa una contraseña corta
        onView(withId(R.id.ContraseñaInicio))
                .perform(typeText("123"), closeSoftKeyboard());

        // Haz clic en el botón de enviar
        onView(withId(R.id.enviarButton)).perform(click());

        // Verifica que aparezca el error correspondiente
        onView(withId(R.id.ContraseñaInicio)).check(matches(withText("La contraseña debe tener más de 6 caracteres")));
    }
}
