/*
 * Appdorid - El Clasico juego de Snake
 * 
 * Autores: Hugo Flórez, Cristina Franco y David Salat
 * 
 */

package com.example.android.snake;

import android.test.ActivityInstrumentationTestCase2;

/**
 * Asegúrese de que la actividad de lanzamiento principal se abre correctamente, 
 * lo cual será verificado por {@ link # testActivityTestCaseSetUpProperly}.
 */
public class SnakeTest extends ActivityInstrumentationTestCase2<Snake> {

    /**
     * Crea un {@link ActivityInstrumentationTestCase2} para {@link Snake} activity.
     */
    public SnakeTest() {
        super(Snake.class);
    }

    /**
     * Verifica que la actividad sometida a prueba se puede iniciar.
     */
    public void testActivityTestCaseSetUpProperly() {
        assertNotNull("La activity puede ser iniciada correctamente", getActivity());
    }
}
