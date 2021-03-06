/*
 * Appdorid - El Clasico juego de Snake
 * 
 * Autores: Hugo Fl�rez, Cristina Franco y David Salat
 * 
 */

package com.example.android.snake;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Snake: actividad para el juego de snake 
 * 
 * Esta es una implementaci� del clasico juego Snake, en el qual se controla
 * una serpiente recorriendo un jardin en busca de manzanas. Cuando se alcaza 
 * una manzana, no solo se aumneta la longitud de la serpiente, adem�s se incrementa
 * la velocidad de movimento. En el caso de chocarse con la pare o con sigo mismo
 * el juego finalizar�. 
 * 
 */
public class Snake extends Activity {

    private SnakeView mSnakeView;
    
    private static String ICICLE_KEY = "snake-view";

    /**
     * Funci�n llamada quando la actividad se crea. Desactiva la barra de t�tulo, 
     * establece las vistas de contenido, y lanza el SnakeView.
     * 
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.snake_layout);

        mSnakeView = (SnakeView) findViewById(R.id.snake);
        mSnakeView.setTextView((TextView) findViewById(R.id.text));

        if (savedInstanceState == null) {
            // Puesta en marcha - creaci�n de un juego nuevo
            mSnakeView.setMode(SnakeView.READY);
        } else {
            // Inicio de restauraci�n del juego
            Bundle map = savedInstanceState.getBundle(ICICLE_KEY);
            if (map != null) {
                mSnakeView.restoreState(map);
            } else {
                mSnakeView.setMode(SnakeView.PAUSE);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Pausa el juego junto con la actividad
        mSnakeView.setMode(SnakeView.PAUSE);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //Almacena el estado del juego
        outState.putBundle(ICICLE_KEY, mSnakeView.saveState());
    }

}
