import java.util.*;

public class Servidor {
    public static final String PORTA_PADRAO = "3000";

    public static void main (String args[])
    {
        if (args.length > 1)
        {
            System.out.print("Uso esperado: java Servidor [PORTA]\n");
            return;
        }

        String novaPorta = Servidor.PORTA_PADRAO;

        if (args.length == 1) {
            novaPorta = args[0];
        }

        ArrayList<Parceiro> usuarios = new ArrayList<Parceiro>();

        AceitadoraDeConexao aceitadoraDeConexao = null;

        try 
        {
            aceitadoraDeConexao = new AceitadoraDeConexao(novaPorta, usuarios);
            aceitadoraDeConexao.start();
        }
        catch (Exception erro)
        {
            System.err.print ("Escolha uma porta apropriada e liberada para uso!\n");
            return;
        }

        for (;;) 
        {
            System.out.println ("O servidor esta ativo! Para desativa-lo,");
            System.out.println ("use o comando \"desativar\"\n");
            System.out.print   ("> ");

            String comandoDigitado = null;
            try 
            {
                comandoDigitado = Teclado.getUmString();
            }
            catch (Exception err)
            {}
            if (comandoDigitado.toLowerCase().equals("desativar")) 
            {
                synchronized (usuarios) 
                {
                    ComunicadoDeDesligamento comunicadoDeDesligamento = new ComunicadoDeDesligamento();

                    for (Parceiro parceiro:usuarios) 
                    {
                        try 
                        {
                            parceiro.receba(comunicadoDeDesligamento);
                            parceiro.adeus();
                        }
                        catch (Exception erro) 
                        {}
                    }

                    System.out.print("O servidor foi desativado!\n");
                    System.exit(0);
                }
            }
            else 
            {
                System.out.print("Comando inválido!\n");
            }
        }
    }
}
