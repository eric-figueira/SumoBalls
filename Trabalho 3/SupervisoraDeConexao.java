import java.io.*;
import java.net.*;
import java.util.*;

public class SupervisoraDeConexao extends Thread
{
    private Parceiro            usuario;
    private Socket              conexao;
    private ArrayList<Parceiro> usuarios;


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
                if (usuarios.size() < 2)
                    usuarios.add(usuario);
                else
                {
                    if (usuarios.get(0) == null) usuarios.add(0,usuario);
                    if (usuarios.get(1) == null) usuarios.add(1,usuario);
                }
                this.usuario.receba(new SetadoraDeJogador(usuarios.indexOf(usuario)));
            }

            for (;;) 
            {
                Comunicado comunicado = this.usuario.envie ();

                if (comunicado==null)
                    return;

                if (comunicado instanceof Ataque)
                {
                    Ataque ataq = (Ataque) comunicado;
                    for (Parceiro parceiro : usuarios)
                        parceiro.receba(ataq);
                }
                else if (comunicado instanceof Movimentacao)
                {
                    Movimentacao  mov = (Movimentacao) comunicado;
                    for (Parceiro parceiro : usuarios)
                        parceiro.receba(mov);
                }
                else if (comunicado instanceof Rotacao)
                {
                    Rotacao rot = (Rotacao) comunicado;
                    for (Parceiro parceiro : usuarios)
                        parceiro.receba(rot);
                }
                else if (comunicado instanceof  ComunicadoDeVitoria)
                {
                    ComunicadoDeVitoria cv = (ComunicadoDeVitoria) comunicado;
//                    ComunicadoDeInicio ci = new ComunicadoDeInicio();
//                    for (Parceiro parceiro : usuarios) {
//                        parceiro.receba(cv);
//                        if (!cv.getHouveDesistencia())
//                            parceiro.receba(ci);
//                    }
                }
                else if (comunicado instanceof ComunicadoDeInicio)
                {
                    ComunicadoDeInicio ci = new ComunicadoDeInicio();
                    for (Parceiro parceiro : usuarios)
                        parceiro.receba(ci);
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
