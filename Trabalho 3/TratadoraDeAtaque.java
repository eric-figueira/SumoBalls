

public class TratadoraDeAtaque extends Thread
{
    private Parceiro servidor = null;

    public TratadoraDeAtaque(Parceiro servidor) throws Exception
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
                if (this.servidor.espie() instanceof Ataque) {
                    Ataque ataque = (Ataque) servidor.envie();
                    Cliente.realizarAtaque(ataque.getPlayerAtacante(), ataque.getDirecaoDoAtaque());
                }
            }
            catch (Exception erro)
            {
                erro.printStackTrace();
            }
        }
    }
}
