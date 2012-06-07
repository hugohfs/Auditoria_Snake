/*
 * Appdorid - El Clasico juego de Snake
 * 
 * Autores: Hugo Flórez, Cristina Franco y David Salat
 * 
 */

package com.example.android.snake;

import java.util.ArrayList;
import java.util.Random;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

/**
 * SnakeView: implementacion de un juego simple de Snake
 * 
 */
public class SnakeView extends TileView {

    private static final String TAG = "SnakeView";

    /**
     * El modo actual de aplicación: READY para correr, RUNNING, o que han perdido ya. 
     * Se utiliza enteros estáticos finales en lugar de una enumeración por motivos de rendimiento.
     * 
     */
    private int mMode = READY;
    public static final int PAUSE = 0;
    public static final int READY = 1;
    public static final int RUNNING = 2;
    public static final int LOSE = 3;

    /**
     * Dirección actual hacia donde se dirige la serpiente.
     * 
     */
    private int mDirection = NORTH;
    private int mNextDirection = NORTH;
    private static final int NORTH = 1;
    private static final int SOUTH = 2;
    private static final int EAST = 3;
    private static final int WEST = 4;

    /**
     * Las etiquetas de los objetos dibujables que se cargarán en la clase TileView
     * 
     */
    private static final int PURPLE_ICON = 1;
    private static final int HEAD_SNAKE = 2;
    private static final int ZONE_ICON = 3;
    private static final int APPLE_ICON = 4;

    /**
     * mScore: utilizado para rastrear el número de manzanas capturadas
     * mMoveDelay: número de milisegundos entre los movimientos de la serpiente.
     * Esto disminuirá al mismo tiempo que se capturen manzanas.
     * 
     */
    private long mScore = 0;
    private long mMoveDelay = 600;
    /**
     * mLastMove: rastrea el tiempo absoluto del último movimiento de la serpiente,
     * y se utiliza para determinar si un movimiento debe hacerse en base a mMoveDelay.
     * 
     */
    private long mLastMove;
    
    /**
     * mStatusText: texto que se muestra al usuario en ciertos estados del ejecución del juego 
     * 
     */
    private TextView mStatusText;

    /**
     * mSnakeTrail: un lista de Coordinates que representa el cuerpo de la serpiente
     * mAppleList: la localizazión secreta de las manzanas que desea comer la serpiente.
     * 
     */
    private ArrayList<Coordinate> mSnakeTrail = new ArrayList<Coordinate>();
    private ArrayList<Coordinate> mAppleList = new ArrayList<Coordinate>();

    /**
     * Función que genera numeros aleatorios
     */
    private static final Random RNG = new Random();

    /**
     * Crea un simple handler que podemos user en caso de que suceda una animación.
     * Nos hemos fijado como objetivo y se puede utilizar la función sleep () para 
     * hacer una actualización/invalidación que se produzca en un timepo futuro.
     * 
     */
    private RefreshHandler mRedrawHandler = new RefreshHandler();

    class RefreshHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            SnakeView.this.update();
            SnakeView.this.invalidate();
        }

        public void sleep(long delayMillis) {
        	this.removeMessages(0);
            sendMessageDelayed(obtainMessage(0), delayMillis);
        }
    };


    /**
     * Construye una SnakeView basada en la inflacion a partir de un XML
     * 
     * @param context
     * @param attrs
     */
    public SnakeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initSnakeView();
   }

    public SnakeView(Context context, AttributeSet attrs, int defStyle) {
    	super(context, attrs, defStyle);
    	initSnakeView();
    }
    
    public int getmNextDirection() {
    	return this.mNextDirection;
    }
    
    public void setmNextDirection(int mNextDir) {
    	this.mNextDirection = mNextDir;
    }
    
    public int getmDirection() {
    	return this.mDirection;
    }
    
    public void setmDirection(int mDir) {
    	this.mDirection = mDir;
    }

    private void initSnakeView() {
        setFocusable(true);

        Resources r = this.getContext().getResources();
        
        resetTiles(5);
        loadTile(PURPLE_ICON, r.getDrawable(R.drawable.purpleball));
        loadTile(HEAD_SNAKE, r.getDrawable(R.drawable.yellowball));
        loadTile(ZONE_ICON, r.getDrawable(R.drawable.grass));
        loadTile(APPLE_ICON, r.getDrawable(R.drawable.apple));
    	
    }
    

    private void initNewGame() {
        mSnakeTrail.clear();
        mAppleList.clear();

        // Se carga la seriente que viene del este y que ha girado
        // hacia el norte
        
        mSnakeTrail.add(new Coordinate(7, 7));
        mSnakeTrail.add(new Coordinate(6, 7));
        mSnakeTrail.add(new Coordinate(5, 7));
        mSnakeTrail.add(new Coordinate(4, 7));
        mSnakeTrail.add(new Coordinate(3, 7));
        mSnakeTrail.add(new Coordinate(2, 7));
        mNextDirection = NORTH;

        // Dos manzanas para empezar
        addRandomApple();
        addRandomApple();

        mMoveDelay = 600;
        mScore = 0;
    }

    /**
     * Dado un ArrayList de coordenadas, necesitamos aplastarla en un array de 
     * enteros antes de que podamos meterlos en un mapa para reducirla y almacenarla. 
     * 
     * @param cvec : un ArrayList de objetos Coordinate
     * @return : un simple array que contiene valores x/y de las coordenadas
     * como [x1,y1,x2,y2,x3,y3...]
     */
    private int[] coordArrayListToArray(ArrayList<Coordinate> cvec) {
        int count = cvec.size();
        int[] rawArray = new int[count * 2];
        for (int index = 0; index < count; index++) {
            Coordinate c = cvec.get(index);
            rawArray[2 * index] = c.x;
            rawArray[2 * index + 1] = c.y;
        }
        return rawArray;
    }

    /**
     * Guardar el estado del juego para que el usuario no pierda
     * nada si el proceso del juego es matado mientras estamos en 
     * background.
     * 
     * @return un Bundle con ese estado de la vista
     */
    public Bundle saveState() {
        Bundle map = new Bundle();

        map.putIntArray("mAppleList", coordArrayListToArray(mAppleList));
        map.putInt("mDirection", Integer.valueOf(mDirection));
        map.putInt("mNextDirection", Integer.valueOf(mNextDirection));
        map.putLong("mMoveDelay", Long.valueOf(mMoveDelay));
        map.putLong("mScore", Long.valueOf(mScore));
        map.putIntArray("mSnakeTrail", coordArrayListToArray(mSnakeTrail));

        return map;
    }

    /**
     * Dada un array reducida de pares de coordenadas, la reconstituimos
     * en un ArrayList de objetos Coordinate  
     * 
     * @param rawArray : [x1,y1,x2,y2,...]
     * @return un ArrayList de Coordinates
     */
    private ArrayList<Coordinate> coordArrayToArrayList(int[] rawArray) {
        ArrayList<Coordinate> coordArrayList = new ArrayList<Coordinate>();

        int coordCount = rawArray.length;
        for (int index = 0; index < coordCount; index += 2) {
            Coordinate c = new Coordinate(rawArray[index], rawArray[index + 1]);
            coordArrayList.add(c);
        }
        return coordArrayList;
    }

    /**
     * Restaurar el estado del juego, si nuestro proceso está siendo relanzado
     * 
     * @param icile es una Bundle que contiene el estado del juego
     */
    public void restoreState(Bundle icicle) {
        setMode(PAUSE);

        mAppleList = coordArrayToArrayList(icicle.getIntArray("mAppleList"));
        mDirection = icicle.getInt("mDirection");
        mNextDirection = icicle.getInt("mNextDirection");
        mMoveDelay = icicle.getLong("mMoveDelay");
        mScore = icicle.getLong("mScore");
        mSnakeTrail = coordArrayToArrayList(icicle.getIntArray("mSnakeTrail"));
    }

    /*
     * Maneja la puslasion de la teclas durante el juego. Actualiza la direccion 
     * en la que nuestra serpiente esta viajando basandose en el DPAD. Ignora
     * los eventos que hacen que la serpiente gire sobre si misma (cambio de sentido).
     * 
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent msg) {

        if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
            if (mMode == READY | mMode == LOSE) {
                /*
                 * Al comienzo del juego, o al final de un ejercicio anterior,
                 * debemos iniciar un nuevo juego.
                 */
                initNewGame();
                setMode(RUNNING);
                update();
                return (true);
            }

            if (mMode == PAUSE) {
                /*
                 * Si el juego estava en pausa, debe seguir donde lo dejamos.
                 */
                setMode(RUNNING);
                update();
                return (true);
            }

            if (mDirection != SOUTH) {
                mNextDirection = NORTH;
            }
            return (true);
        }

        if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            if (mDirection != NORTH) {
                mNextDirection = SOUTH;
            }
            return (true);
        }

        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            if (mDirection != EAST) {
                mNextDirection = WEST;
            }
            return (true);
        }

        if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            if (mDirection != WEST) {
                mNextDirection = EAST;
            }
            return (true);
        }

        return super.onKeyDown(keyCode, msg);
    }

    /**
     * Establece el TextView que se utilizará para dar información 
     * (como "Game Over") para el usuario.
     * 
     * @param newView
     */
    public void setTextView(TextView newView) {
        mStatusText = newView;
    }

    /**
     * Actualiza el modo actual de la apliación (RUNNING o PAUSED o parecido)
     * así como establece la visibilidad de TextView para la notificación.
     * 
     * @param newMode
     */
    public void setMode(int newMode) {
        int oldMode = mMode;
        mMode = newMode;

        if (newMode == RUNNING & oldMode != RUNNING) {
            mStatusText.setVisibility(View.INVISIBLE);
            update();
            return;
        }

        Resources res = getContext().getResources();
        CharSequence str = "";
        if (newMode == PAUSE) {
            str = res.getText(R.string.mode_pause);
        }
        if (newMode == READY) {
            str = res.getText(R.string.mode_ready);
        }
        if (newMode == LOSE) {
            str = res.getString(R.string.mode_lose_prefix) + mScore
                  + res.getString(R.string.mode_lose_suffix);
        }

        mStatusText.setText(str);
        mStatusText.setVisibility(View.VISIBLE);
    }

    /**
     * Selecciona una localización aleatoria en el jardin que no este ocupada por
     * la serpiente. Se podria dar el caso que la función entrara en un bucle 
     * infinito si la serpiente ocupa todo el jardin, pero se deja al descubierto
     * de este premio a un verdadero jugador excelente del juego.
     * 
     */
    private void addRandomApple() {
        Coordinate newCoord = null;
        boolean found = false;
        while (!found) {
            // Elige una nueva localización para nuestra manzana
            int newX = 1 + RNG.nextInt(mXTileCount - 2);
            int newY = 1 + RNG.nextInt(mYTileCount - 2);
            newCoord = new Coordinate(newX, newY);

            // Se asegura que la localización nueva no esta ocupada por la serpiente
            found = true;
            int snakelength = mSnakeTrail.size();
            for (int index = 0; index < snakelength; index++) {
                if (mSnakeTrail.get(index).equals(newCoord)) {
                	found = false;
                }
            }
        }
        if (newCoord == null) {
            Log.e(TAG, "Somehow ended up with a null newCoord!");
        }
        mAppleList.add(newCoord);
    }


    /**
     * Maneja el ciclo basico de actualización, comprobando si estamos en 
     * el estado de ejecución, determinando si un movimiento se puede realizar,
     * actualizando la localozazión de la serpiente. 
     */
    public void update() {
        if (mMode == RUNNING) {
            long now = System.currentTimeMillis();

            if (now - mLastMove > mMoveDelay) {
                clearTiles();
                updateWalls();
                updateSnake();
                updateApples();
                mLastMove = now;
            }
            mRedrawHandler.sleep(mMoveDelay);
        }

    }

    /**
     * Dibuja paredes.
     * 
     */
    private void updateWalls() {
        for (int x = 0; x < mXTileCount; x++) {
            setTile(ZONE_ICON, x, 0);
            setTile(ZONE_ICON, x, mYTileCount - 1);
        }
        for (int y = 1; y < mYTileCount - 1; y++) {
            setTile(ZONE_ICON, 0, y);
            setTile(ZONE_ICON, mXTileCount - 1, y);
        }
    }

    /**
     * Dibuja algunas manzanas.
     * 
     */
    private void updateApples() {
        for (Coordinate c : mAppleList) {
            setTile(APPLE_ICON, c.x, c.y);
        }
    }

    /**
     * Averigua el caminio que lleva la serpiente, mira si colisiona con algo 
     * (paredes, ella misma o una manzana). Si no muere, añade una unidad en la 
     * parte delantera y resta de la parte trasera de la serpiente con el fin de 
     * simular movimiento. Si queremos que la serpiente crezca, no restaremos de
     * la parte trasera. 
     * 
     */
    private void updateSnake() {
        boolean growSnake = false;

        // toma la serpiente por la cabeza
        Coordinate head = mSnakeTrail.get(0);
        Coordinate newHead = new Coordinate(1, 1);

        mDirection = mNextDirection;

        switch (mDirection) {
        case EAST: {
            newHead = new Coordinate(head.x + 1, head.y);
            break;
        }
        case WEST: {
            newHead = new Coordinate(head.x - 1, head.y);
            break;
        }
        case NORTH: {
            newHead = new Coordinate(head.x, head.y - 1);
            break;
        }
        case SOUTH: {
            newHead = new Coordinate(head.x, head.y + 1);
            break;
        }
        default:
        	Log.e(TAG, "Some error in switch mDirection. Method updateSnake");
        	break;
        }

        //Detectar colisiones
        detectColision(newHead);
        detectColisionWithSanke(newHead);

        // Buscar manzanas
        growSnake = searchApples(newHead);

        // ponemos una nueva cabeza en el ArrayList y sacamos una unidad de la cola
        updateSnakeHead(growSnake,newHead);
    }

    /*
     * Añade una nueva cabeza en el ArrayList y resta una unidad de la cola
     */
     private void updateSnakeHead(Boolean growSnake, Coordinate newHead) {
        mSnakeTrail.add(0, newHead);
        // excepto si queremos que la serpiente crezca
        if (!growSnake) {
            mSnakeTrail.remove(mSnakeTrail.size() - 1);
        }

        int index = 0;
        for (Coordinate c : mSnakeTrail) {
            if (index == 0) {
                setTile(HEAD_SNAKE, c.x, c.y);
            } else {
                setTile(PURPLE_ICON, c.x, c.y);
            }
            index++;
        }
	}

	/*
     * Buscar manzanas
     */
    private boolean searchApples(Coordinate newHead) {
        int applecount = mAppleList.size();
        for (int appleindex = 0; appleindex < applecount; appleindex++) {
            Coordinate c = mAppleList.get(appleindex);
            if (c.equals(newHead)) {
                mAppleList.remove(c);
                addRandomApple();
                
                mScore++;
                mMoveDelay *= 0.9;

                return true;
            }
        }
		return false;
	}

	/*
     *  Busca colisiones con la serpiente misma
     */
    private void detectColisionWithSanke(Coordinate newHead) {
        int snakelength = mSnakeTrail.size();
        for (int snakeindex = 0; snakeindex < snakelength; snakeindex++) {
            Coordinate c = mSnakeTrail.get(snakeindex);
            if (c.equals(newHead)) {
                setMode(LOSE);
                return;
            }
        }
		
	}

	/*
     *  Detección de una colisión
     *  En este caso tenemos una pared de grosor de una unidad alrededor del jardin
     */
    private void detectColision(Coordinate newHead) {
        if ((newHead.x < 1) || (newHead.y < 1) || (newHead.x > mXTileCount - 2)
                || (newHead.y > mYTileCount - 2)) {
            setMode(LOSE);
            return;

        }
		
	}
    
}
