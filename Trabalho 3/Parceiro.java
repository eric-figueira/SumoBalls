import java.net.*;
import java.io.*;
import java.util.concurrent.Semaphore;


public class Parceiro 
{
    private Socket conexao;
    private ObjectInputStream receptor;
    private ObjectOutputStream transmissor;

    private Comunicado proximoComunicado = null;

    private Semaphore semaforo = new Semaphore(1, true);
    
    public Parceiro (Socket conexao, ObjectInputStream receptor, ObjectOutputStream transmissor) throws Exception
    {
        if (conexao == null) { throw new Exception("Conexao ausente!"); }
        if (receptor == null) { throw new Exception("Receptor ausente!"); }
        if (transmissor == null) { throw new Exception("Transmissor ausente!"); }

        this.conexao = conexao;
        this.receptor = receptor;
        this.transmissor = transmissor;
    }


    public void receba (Comunicado x) throws Exception
    {
        try
        {
            transmissor.writeObject(x);
            transmissor.flush();
        }
        catch (IOException erro) { erro.printStackTrace(); }
    }


    public Comunicado espie () throws Exception // revisar
    {
        try
        {
            semaforo.acquireUninterruptibly();
            if (this.proximoComunicado == null) 
                this.proximoComunicado = (Comunicado) receptor.readObject();
            semaforo.release();

            return this.proximoComunicado;
        }
        catch (Exception erro) { throw new Exception("Erro na espionagem!"); }
    }


    public Comunicado envie () throws Exception // revisar
    {
        try
        {
            if (this.proximoComunicado == null)
                this.proximoComunicado = (Comunicado) receptor.readObject();
            
            Comunicado ret = this.proximoComunicado;
            this.proximoComunicado = null;
            return ret;
        }
        catch (Exception erro) { erro.printStackTrace(); }
        return null;
    }


    public void adeus () throws Exception
    {
        try 
        {
            conexao.close();
            transmissor.close();
            receptor.close();
        }
        catch (Exception erro) { throw new Exception("Deu merda"); }
    }
}