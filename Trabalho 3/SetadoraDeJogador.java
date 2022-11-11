

public class SetadoraDeJogador extends Comunicado
{
    private final int indexJogador;

    public SetadoraDeJogador(int index) { this.indexJogador = index;}

    public int getIndexJogador() { return indexJogador; }

    @Override
    public String toString() { return this.indexJogador + ""; }
}