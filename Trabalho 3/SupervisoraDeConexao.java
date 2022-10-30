import java.io.*;
import java.net.*;
import java.util.*;

public class SupervisoraDeConexao extends Thread
{
    private Parceiro            usuario;
    private Socket              conexao;
    private ArrayList<Parceiro> usuarios;
    // private int x, y;
    // private char direcao;

    public SupervisoraDeConexao (Socket conexao, ArrayList<Parceiro> usuarios) throws Exception 
    {
        if (conexao == null) 
            throw new Exception ("Conexão ausente!");
        
        if (usuarios == null)
            throw new Exception ("Usuários ausentes!");

        this.conexao = conexao;
        this.usuarios = usuarios;
    }

    public void run () 
    {
        ObjectOutputStream transmissor;
        try 
        {
            transmissor = new ObjectOutputStream(this.conexao.getOutputStream());
        }
        catch (Exception err) 
        {
            return;
        }

        ObjectInputStream receptor;
        try 
        {
            receptor = new ObjectInputStream(this.conexao.getInputStream());
        }
        catch (Exception err) 
        {
            try
            {
                transmissor.close();
            }
            catch (Exception falha)
            {} 
            
            return;
        }

        try 
        {
            this.usuario = new Parceiro(this.conexao, receptor, transmissor);
        }
        catch (Exception err) 
        {}

        try 
        {
            synchronized (usuarios) 
            {
                this.usuarios.add(usuario);
            }

            for (;;) 
            {
                Comunicado comunicado = this.usuario.envie ();

                if (comunicado==null)
                    return;

                if (comunicado instanceof PedidoDeAtaque)
                {

                }

                else if (comunicado instanceof PedidoDeMovimentacao) 
                {

                }

                else if (comunicado instanceof PedidoParaSair) 
                {
                    synchronized (usuarios) 
                    {
                        usuarios.remove(usuario);
                    }
                    this.usuario.adeus();
                }

            }
        }
        catch (Exception erro)
        {
            try
            {
                transmissor.close ();
                receptor   .close ();
            }
            catch (Exception err)
            {} 

            return;
        }
    }
}
