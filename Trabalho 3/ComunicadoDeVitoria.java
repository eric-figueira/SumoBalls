

public class ComunicadoDeVitoria extends Comunicado 
{  
    private char playerVencedor;
    private boolean desistencia;

    public ComunicadoDeVitoria (char playerVencedor, boolean desistencia)
    {
        this.playerVencedor = playerVencedor;
        this.desistencia = desistencia;
    }

    public char getPlayerVencedor() { return this.playerVencedor; }
    public boolean getHouveDesistencia() { return this.desistencia; }

    @Override
    public String toString() 
    {
        return this.playerVencedor + ".Desistencia: " + this.desistencia;
    }
    
}