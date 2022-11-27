

public class TratadoraDeComunicadoDeVitoria extends Thread
{
    private Parceiro servidor;

    public TratadoraDeComunicadoDeVitoria(Parceiro servidor) throws Exception
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
                if (c instanceof ComunicadoDeVitoria)
                {
                    if (((ComunicadoDeVitoria) c).getHouveDesistencia()) {
                        Cliente.Janela.MostrarMensagemDeErro("O jogador se desconectou da partida!");
                        System.exit(0);
                    }
                }
            }
            catch (Exception erro)
            {}
        }
    }
}