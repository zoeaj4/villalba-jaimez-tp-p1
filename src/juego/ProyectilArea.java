package juego;

import java.awt.Image;
import entorno.Herramientas;
import entorno.Entorno;


public class ProyectilArea extends Proyectil {

    private int radioExplosion = 100;
    private Image imagenArea;
    
    public ProyectilArea(double x, double y,
                         double destinoX, double destinoY) {
        super(x, y, destinoX, destinoY);
        
        this.imagenArea =
        	    Herramientas.cargarImagen("proyectilArea.png");
        
    }

    public int getRadioExplosion() {
        return radioExplosion;
    }
    @Override
    public void dibujarse(Entorno entorno) {

        entorno.dibujarImagen(
            imagenArea,
            getX(),
            getY(),
            getAngulo(),
            0.08
        );
    }
}