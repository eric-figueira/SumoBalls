import java.net.*;


public class TratadoraDeComunicadoDeDesligamento extends Thread
{
    private Parceiro servidor;

    public TratadoraDeComunicadoDeDesligamento(Parceiro servidor) throws Exception
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
                if (this.servidor.espie() instanceof ComunicadoDeDesligamento)
                {
                    Cliente1.Janela.MostrarMensagemDeErro("VAI DAR MERDA");
                }
            }
            catch (Exception erro)
            {}
        }
    }
}
