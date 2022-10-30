import java.util.*;
import java.net.*;

public class AceitadoraDeConexao extends Thread
{
    private ServerSocket pedido = null;
    private ArrayList<Parceiro> usuarios = null;

    public AceitadoraDeConexao (String porta, ArrayList<Parceiro> usuarios) throws Exception
    {
        if (porta == null)
            throw new Exception ("Porta ausente!");

        try 
        {
            pedido = new ServerSocket(Integer.parseInt(porta));
        }
        catch (Exception erro) 
        {
            throw new Exception ("Porta inválida!");
        }

        if (usuarios == null)
            throw new Exception ("Usuário ausente!");

        this.usuarios = usuarios;
    }

    public void run () 
    {
        for (;;) 
        {
            Socket conexao = null;

            try 
            {
                conexao = this.pedido.accept();
            }
            catch (Exception erro) 
            {
                continue;
            }

            SupervisoraDeConexao supervisoraDeConexao = null;

            try 
            {
                supervisoraDeConexao = new SupervisoraDeConexao (conexao, usuarios);
            }
            catch (Exception erro) 
            {}

            supervisoraDeConexao.start();
        }
    }
}
