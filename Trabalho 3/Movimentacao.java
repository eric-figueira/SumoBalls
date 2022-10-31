

public class Movimentacao extends Comunicado
{
    private char playerMovimentante,
                 direcaoDoMovimento;

    public Movimentacao(char playerMovimentante, char direcaoDoMovimento)
    {
        this.playerMovimentante = playerMovimentante;
        this.direcaoDoMovimento = direcaoDoMovimento;
    }

    public char getPlayerMovimentante() { return this.playerMovimentante; }
    
    public char getDirecaoDoMovimento() { return this.direcaoDoMovimento; }

    @Override
    public String toString()
    {
        return this.playerMovimentante +  ":" + this.direcaoDoMovimento;
    }
}