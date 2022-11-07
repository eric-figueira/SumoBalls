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
                    //Cliente.Janela.MostrarMensagemDeErro("O servidor será desativado em breve. " +
                            //"Volte a tentar novamente mais tarde!");
                    System.exit(0);
                }
            }
            catch (Exception erro)
            {}
        }
    }
}
