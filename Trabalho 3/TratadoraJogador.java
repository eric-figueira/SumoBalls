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
                    Rotacao rotacao = (Rotacao) servidor.envie();
                    Cliente.realizarRotacao(rotacao.getPlayerRotante(),rotacao.getDirecaoDaRotacao());
                }
                else if (this.servidor.espie() instanceof Ataque)
                {
                    Ataque ataque = (Ataque) servidor.envie();
                    Cliente.realizarAtaque(ataque.getPlayerAtacante(),ataque.getDirecaoDoAtaque());
                }
                else if (this.servidor.espie() instanceof ComunicadoDeVitoria)
                {
                    ComunicadoDeVitoria cv = (ComunicadoDeVitoria) servidor.envie();
                    Cliente.Janela.comunicarVitoria(cv.getPlayerVencedor(), cv.getHouveDesistencia());
                }
                else if (this.servidor.espie() instanceof SetadoraDeJogador)
                {
                    SetadoraDeJogador setPlayer = (SetadoraDeJogador) servidor.envie();
                    Cliente.setPlayer(setPlayer.getIndexJogador());
                }
                else if (this.servidor.espie() instanceof ComunicadoDeInicio)
                {
                    servidor.envie();
                    Cliente.iniciar();
                }
            }
            catch (Exception erro)
            {
                erro.printStackTrace();
            }
        }
    }
}
