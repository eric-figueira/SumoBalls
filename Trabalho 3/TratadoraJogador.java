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
                Comunicado c = this.servidor.espie();
                if (c instanceof Movimentacao)
                {
                    Movimentacao movimento = (Movimentacao) servidor.envie ();
                    Cliente.realizarMovimentacao(movimento.getPlayerMovimentante(),movimento.getDirecaoDoMovimento());
                }
                else if (c instanceof Rotacao)
                {
                    Rotacao rotacao = (Rotacao) servidor.envie();
                    Cliente.realizarRotacao(rotacao.getPlayerRotante(),rotacao.getDirecaoDaRotacao());
                }
                else if (c instanceof Ataque)
                {
                    Ataque ataque = (Ataque) servidor.envie();
                    Cliente.realizarAtaque(ataque.getPlayerAtacante(),ataque.getDirecaoDoAtaque());
                }
                else if (c instanceof ComunicadoDeVitoria)
                {
                    ComunicadoDeVitoria cv = (ComunicadoDeVitoria) servidor.envie();
                    Cliente.Janela.comunicarVitoria(cv.getPlayerVencedor(), cv.getHouveDesistencia());
                }
                else if (c instanceof SetadoraDeJogador)
                {
                    SetadoraDeJogador setPlayer = (SetadoraDeJogador) servidor.envie();
                    Cliente.setPlayer(setPlayer.getIndexJogador());
                }
                else if (c instanceof ComunicadoDeInicio) {
                    ComunicadoDeInicio ci = (ComunicadoDeInicio) servidor.envie();
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
