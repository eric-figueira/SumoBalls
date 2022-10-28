

public class PedidoDeAtaque extends Comunicado
{
    private char playerAtacante,
                 direcaoDoAtaque;

    public PedidoDeAtaque(char playerAtacante, char direcaoDoAtaque) 
    {
        this.playerAtacante = playerAtacante;
        this.direcaoDoAtaque = direcaoDoAtaque;
    }

    public char getPlayerAtacante() { return this.playerAtacante; }

    public char getDirecaoDoAtaque() { return this.direcaoDoAtaque; }

    @Override
    public String toString() 
    {
        return playerAtacante + ":" + direcaoDoAtaque;
    }
}
