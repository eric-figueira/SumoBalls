

public class ComunicadoDeVitoria extends Comunicado 
{  
    private char playerVencedor;

    public ComunicadoDeVitoria (char playerVencedor)
    {
        this.playerVencedor = playerVencedor;
    }

    public char getPlayerVencedor() { return this.playerVencedor; }

    @Override
    public String toString() 
    {
        return this.playerVencedor + "";
    }
    
}