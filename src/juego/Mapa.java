package juego;
 
import java.awt.Image;
import entorno.Entorno;
import entorno.Herramientas;
 
public class Mapa {
 
    public static final int ANCHO_MUNDO    = 5000; // tamaño total del nivel en píxeles
    public static final int ALTO_PANTALLA  = 600;
    public static final int ANCHO_PANTALLA = 800;
 
    // Cuántos píxeles hay que "correr" el fondo hacia la izquierda para que
    // la princesa aparezca centrada. Valor en coordenadas mundo.
    private double desplazamientoMapaX;
 
    private Image fondo;
    private Image imagenCastillo;
    private Isla[] islas;
 
    // Posición y tamaño del castillo (meta del juego), en coordenadas mundo.
    private static final double X_CASTILLO     = ANCHO_MUNDO - 100;
    private static final double Y_CASTILLO     = 390;
    private static final double ANCHO_CASTILLO = 200;
    private static final double ALTO_CASTILLO  = 250;
 
    public Mapa() {
        this.desplazamientoMapaX = 0;
        this.fondo          = Herramientas.cargarImagen("fondo.jpg");
        this.imagenCastillo = Herramientas.cargarImagen("castillo.png");
        generarIslas();
    }
 
    // Crea todas las islas del nivel de forma procedural (con algo de aleatoriedad)
    private void generarIslas() {
        int cantPiso   = 15; // islas del piso (nivel bajo, y= 565)
        int cantNivel1 = 10; // plataformas intermedias (y=400)
        int cantNivel2 = 10; // plataformas altas (y= 250)
        int total      = cantPiso + cantNivel1 + cantNivel2; // = 35 islas en total
 
        islas = new Isla[total];  // se crea el arreglo con 35 lugares
 
        // --- Piso
        // Las islas del piso se colocan una al lado de la otra con un hueco fijo.
        // No hay aleatoriedad acá: siempre quedan en el mismo lugar.
        // anchoPiso = 200 - cada isla mide 200px de ancho
        // huecos = 150 - entre isla e isla hay 150px de espacio vacío
        // xPiso empieza en 0 y se va corriendo a la derecha cada vuelta.
        double anchoPiso = 200;
        double huecos    = 150;
        double xPiso     = 0;
        for (int i = 0; i < cantPiso; i++) {
            islas[i] = new Isla(xPiso + anchoPiso / 2, 565, anchoPiso, 50);
            xPiso += anchoPiso + huecos; // avanza el ancho + el hueco
        }
 
        // --- NIVEL 1 (plataformas del medio, y = 400) 
        // En vez de ponerlas una al lado de la otra, se divide el mundo en partes iguales y se coloca una isla en cada parte.
        // A esa x base se le suma un número aleatorio entre -50 y +50,  para que no queden todas perfectamente alineadas
        // Las islas del nivel 1 se guardan a partir de la posición cantPiso (15) en el arreglo, para no pisar las islas del piso.
        double paso1 = (double) ANCHO_MUNDO / (cantNivel1 + 1);
        for (int i = 0; i < cantNivel1; i++) {
            double x     = paso1 * (i + 1) + (Math.random() * 100 - 50); // + -50 px de variación
            double y     = 400 + (Math.random() * 40 - 20);               // + - 20 px de variación
            double ancho = 120 + (Math.random() * 80);                    // entre 120 y 200 px
            islas[cantPiso + i] = new Isla(x, y, ancho, 30);
        }
 
        // --- NIVEL 2 (plataformas altas, y = 250) ---
        // Exactamente igual que el nivel 1, pero más arriba (y base = 250)
        // y con ancho mínimo un poco menor (100px en vez de 120px).
        // Se guardan en el arreglo a partir de la posición cantPiso + cantNivel1
        // (= 15 + 10 = 25), para no pisar las islas anteriores.
        double paso2 = (double) ANCHO_MUNDO / (cantNivel2 + 1);
        for (int i = 0; i < cantNivel2; i++) {
            double x     = paso2 * (i + 1) + (Math.random() * 100 - 50);
            double y     = 250 + (Math.random() * 40 - 20);
            double ancho = 100 + (Math.random() * 80);
            islas[cantPiso + cantNivel1 + i] = new Isla(x, y, ancho, 30);
        }
    }
 
    // Calcula desplazamientoMapaX para que la princesa quede centrada en pantalla.
    // princesaXMundo = posición absoluta de la princesa en el mundo.
    // El desplazamiento se limita entre 0 y (ANCHO_MUNDO - ANCHO_PANTALLA) para no mostrar el vacío más allá de los bordes del mundo.
    public void actualizarDesplazamiento(double princesaXMundo) {
        // La idea es mantener a la princesa siempre en el centro de la pantalla. Si la princesa está en x=1000 del mundo, la cámara tiene que mostrar desde x=600 hasta x=1400 del mundo (con pantalla de 800px).
        // Eso significa correr la cámara 600px → desplazamiento = 600. Fórmula: desplazamiento = xPrincesa - mitadDePantall
        double mitadPantalla = ANCHO_PANTALLA / 2.0;
        double nuevoOffset   = princesaXMundo - mitadPantalla;
 
        // Límites: el desplazamiento no puede ser negativo (la cámara no puede ir más a la izquierda que el inicio del mundo) ni puede superar ANCHO_MUNDO - ANCHO_PANTALLA (no puede ir más a la derecha que elfinal del mundo, o se vería el vacío de fondo)

        double maxOffset = ANCHO_MUNDO - ANCHO_PANTALLA;
        if (nuevoOffset < 0)         nuevoOffset = 0;
        if (nuevoOffset > maxOffset) nuevoOffset = maxOffset;
 
        this.desplazamientoMapaX = nuevoOffset;
    }
 
    // Dibuja el fondo, el castillo y todas las islas.
    // Cada objeto usa desplazamientoMapaX para convertir su posición mundo → pantalla.
    public void dibujarse(Entorno entorno) {
        // El fondo siempre se dibuja centrado en la ventana (no se mueve con el scroll).
        entorno.dibujarImagen(fondo, ANCHO_PANTALLA / 2.0, ALTO_PANTALLA / 2.0, 0, 1.0);
 
        // El castillo sí existe en el mundo (al final, en x=4900). Se convierte a posición en pantalla restando el desplazamiento. Solo se dibuja si está visible: si su borde derecho ya entró en pantalla (> -ANCHO_CASTILLO) y su borde izquierdo no salió por la derecha (< ANCHO_PANTALLA + ANCHO_CASTILLO).
        // Esto evita dibujarlo cuando está lejos y no se ve.
        double castilloPantallaX = X_CASTILLO - desplazamientoMapaX;
        if (castilloPantallaX > -ANCHO_CASTILLO && castilloPantallaX < ANCHO_PANTALLA + ANCHO_CASTILLO) {
            entorno.dibujarImagen(imagenCastillo, castilloPantallaX, Y_CASTILLO, 0, 0.3);
        }
 
        // Dibuja cada isla pasando el desplazamiento para que se posicione en pantalla.
        for (Isla isla : islas) {
            isla.dibujarse(entorno, desplazamientoMapaX);
        }
    }
 
    public double getDesplazamientoMapaX() { 
    	return desplazamientoMapaX; 
    	}
    public Isla[] getIslas() { 
    	return islas; 
    	}
    public double getXCastillo() { 
    	return X_CASTILLO; 
    	}
}
