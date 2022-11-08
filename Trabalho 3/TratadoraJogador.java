import java.net.*;

public class TratadoraJogador extends Thread
{
    private Parceiro servidor = null;

    public TratadoraJogador(Parceiro servidor) throws Exception
    {
        if (servidor == null)
            throw new Exception("Porta inválida!");

        this.servidor = servidor;
    }

    public void run()
    {
        for (;;)
        {
            try
            {
                if (this.servidor.espie() instanceof Movimentacao)
                {
                    synchronized (this)
                    {
                        Movimentacao movimento = (Movimentacao) servidor.envie();
                        Cliente.realizarMovimentacao(movimento.getPlayerMovimentante(), movimento.getDirecaoDoMovimento());
                    }
                }
                else if (this.servidor.espie() instanceof Rotacao)
                {
                    synchronized (this)
                    {
                        Rotacao rotacao = (Rotacao) servidor.envie();
                        Cliente.realizarRotacao(rotacao.getPlayerRotante(), rotacao.getDirecaoDaRotacao());
                    }
                }
                else if (this.servidor.espie() instanceof Ataque)
                {
                    synchronized (this)
                    {
                        Ataque ataque = (Ataque) servidor.envie();
                        Cliente.realizarAtaque(ataque.getPlayerAtacante(), ataque.getDirecaoDoAtaque());
                    }
                }
            }
            catch (Exception erro)
            { erro.printStackTrace(); }
        }
    }
}
