package com.example.android.snake;

/**
 * Una clase simple que contiene dos valores enteros y función de comparación.
 * 
 * Simple class containing two integer values and a comparison function.
 * Se utiliza para manejar las coordenadas de la serpiente y de las manzanas.
 */
public class Coordinate {
    public int x;
    public int y;

    public Coordinate(int newX, int newY) {
        x = newX;
        y = newY;
    }

    public boolean equals(Coordinate other) {
    	return (x == other.x && y == other.y);
    }

    @Override
    public String toString() {
        return "Coordinate: [" + x + "," + y + "]";
    }
}
