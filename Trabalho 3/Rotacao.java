

public class Rotacao extends Comunicado
{
    private char playerRotante,
                 direcaoRotacao;

    public Rotacao(char playerRotante, char direcaoRotacao)
    {
        this.playerRotante = playerRotante;
        this.direcaoRotacao = direcaoRotacao;
    }

    public char getPlayerRotante() { return this.playerRotante; }

    public char getDirecaoDaRotacao() { return this.direcaoRotacao; }

    @Override
    public String toString()
    {
        return this.playerRotante +  ":" + this.direcaoRotacao;
    }
}
