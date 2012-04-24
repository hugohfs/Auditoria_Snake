/*
 * Appdorid - El Clasico juego de Snake
 * 
 * Autores: Hugo Flórez, Cristina Franco y David Salat
 * 
 */

package com.example.android.snake;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

/**
 * Snake: actividad para el juego de snake 
 * 
 * Esta es una implementació del clasico juego Snake, en el qual se controla
 * una serpiente recorriendo un jardin en busca de manzanas. Cuando se alcaza 
 * una manzana, no solo se aumneta la longitud de la serpiente, además se incrementa
 * la velocidad de movimento. En el caso de chocarse con la pare o con sigo mismo
 * el juego finalizará. 
 * 
 */
public class Snake extends Activity {

    private SnakeView mSnakeView;
    
    private static String ICICLE_KEY = "snake-view";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.snake_layout);

        mSnakeView = (SnakeView) findViewById(R.id.snake);
        mSnakeView.setTextView((TextView) findViewById(R.id.text));

        if (savedInstanceState == null) {
            mSnakeView.setMode(SnakeView.READY);
        } else {
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
        mSnakeView.setMode(SnakeView.PAUSE);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBundle(ICICLE_KEY, mSnakeView.saveState());
    }

}
