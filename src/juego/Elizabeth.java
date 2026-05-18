package juego;

import java.awt.Image;
import entorno.Entorno;
import entorno.Herramientas;

public class Elizabeth {
	
	// Posición en el MUNDO (no en pantalla). x puede superar los 800 px
    private double x, y;
    private double ancho, alto;
    private double velocidadY;
    private boolean enElAire;
    private int mirando; // 1 = Derecha, -1 = Izquierda

    // Imagenes
    private Image imagenDerecha;
    private Image imagenIzquierda;
    private Image imagenSaltoDerecha;
    private Image imagenSaltoIzquierda;
    
    
    // GRAVEDAD: cuánta velocidad vertical se suma cada tick Al aplicarse tick a tick, la caída se acelera de forma natural.
    private static final double GRAVEDAD = 1.0;
    private static final double VELOCIDAD_MOVIMIENTO = 4;
    private static final double IMPULSO_SALTO= -18;

    public Elizabeth(double x, double y) {
        this.x = x;
        this.y = y;
        this.ancho = 50;
        this.alto  = 70;
        this.velocidadY = 0;
        this.enElAire   = true;
        this.mirando    = 1; // empieza mierando a la derecha

        this.imagenDerecha = Herramientas.cargarImagen("elizabeth_derecha.png");
        this.imagenIzquierda = Herramientas.cargarImagen("elizabeth_izquierda.png");
        this.imagenSaltoDerecha = Herramientas.cargarImagen("elizabeth_salto_derecha.png");
        this.imagenSaltoIzquierda = Herramientas.cargarImagen("elizabeth_salto_izquierda.png");
        
    }

    // Dibuja la princesa en pantalla
    // desplazamientoMundoX es el scroll del mapa; se resta a x para obtener la posición real en la ventana:
    // xPantalla = xMundo - desplazamiento
    public void dibujarse(Entorno entorno, double desplazamientoMundoX) {
        // Elige la imagen según si está en el aire y hacia dónde mira
        Image imagenAUsar;
        if (enElAire) {
            imagenAUsar = (mirando == 1) ? imagenSaltoDerecha : imagenSaltoIzquierda;
        } else {
            imagenAUsar = (mirando == 1) ? imagenDerecha : imagenIzquierda;
        }
        
     // Convierte de coordenada mundo a coordenada pantalla
        double xPantalla = x - desplazamientoMundoX;
        entorno.dibujarImagen(imagenAUsar, xPantalla, this.y, 0, 0.4);
    }


//    public void dibujarse(Entorno entorno) {
  //      dibujarse(entorno, 0);
    //}

    // Suma VELOCIDAD_MOVIMIENTO a x y actualiza la dirección
    public void moverDerecha() {
        this.x += VELOCIDAD_MOVIMIENTO;
        this.mirando = 1;
    }
 
    // Resta VELOCIDAD_MOVIMIENTO a x, pero sólo si el resultado sigue siendo > 0
    // (impide salir por el borde izquierdo del mundo)
    public void moverIzquierda() {
        if (this.x - VELOCIDAD_MOVIMIENTO > 0) {
            this.x -= VELOCIDAD_MOVIMIENTO;
        }
        this.mirando = -1;
    }
 
    // Sólo salta si ya está apoyada en algo (evita doble salto)
    // IMPULSO_SALTO es negativo, así que la velocidadY se vuelve negativa
    // y el personaje sube (y disminuye en pantalla)
    public void saltar() {
        if (!enElAire) {
            this.velocidadY = IMPULSO_SALTO;
            this.enElAire   = true;
        }
    }
 
    // Cada tick: la gravedad incrementa velocidadY y esa velocidad se suma a y
    // El resultado es una aceleración gradual hacia abajo.
    public void aplicarGravedad() {
        this.velocidadY += GRAVEDAD;
        this.y += velocidadY;
    }
 
    // Se llama cuando la princesa pisa una isla.
    // ySuperficie es el tope de la isla; la princesa se coloca con sus pies justo ahí
    public void aterrizar(double ySuperficie) {
        this.y = ySuperficie - (this.alto / 2); // centra el sprite encima de la isla
        this.velocidadY = 0;
        this.enElAire   = false;
    }
 
    // Se llama cuando la cabeza choca contra el fondo de una isla al saltar
    // yFondo es la parte baja de la isla; empuja a la princesa hacia abajo
    public void pegarTecho(double yFondo) {
        this.y = yFondo + (this.alto / 2); // la baja hasta quedar debajo de la isla
        this.velocidadY = 0; // corta el impulso hacia arriba
    }
 
    // Devuelve true si la princesa está subiendo (velocidad negativa = hacia arriba).
    public boolean estaSubiendo() {
        return this.velocidadY < 0;
    }
 
    public void setEnElAire(boolean b) {
        this.enElAire = b;
    }
    
    // Setters y Getters
    public double getX()     { 
    	return x; 
    	}
    public double getY()     { 
    	return y; 
    	}
    public double getAncho() { 
    	return ancho; 
    	}
    public double getAlto()  { 
    	return alto; 
    	}
}