//import java.net.*;


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
                    Movimentacao movimento = (Movimentacao) servidor.envie ();
                    Cliente.realizarMovimentacao(movimento.getPlayerMovimentante(),movimento.getDirecaoDoMovimento());
                }
                else if (this.servidor.espie() instanceof Rotacao)
                {
                    Cliente.Janela.MostrarMensagemDeErro("POR FAVOR FUNCIONA 1");
                    Rotacao rotacao = (Rotacao) servidor.envie();
                    Cliente.Janela.MostrarMensagemDeErro("POR FAVOR FUNCIONA 2");
                    Cliente.realizarRotacao(rotacao.getPlayerRotante(),rotacao.getDirecaoDaRotacao());
                    Cliente.Janela.MostrarMensagemDeErro("POR FAVOR FUNCIONA 3");
                }
                else if (this.servidor.espie() instanceof Ataque)
                {
                    Ataque ataque = (Ataque) servidor.envie();
                    Cliente.realizarAtaque(ataque.getPlayerAtacante(),ataque.getDirecaoDoAtaque());
                }
            }
            catch (Exception erro)
            {}
        }
    }
}
