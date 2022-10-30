import java.net.*;
import java.io.*;


// public class Cliente 
// {
//     public static final String HOST_PADRAO = "localhost";
//     public static final int PORTA_PADRAO = 3000;
//     public static void main (String args[])
//     {
//         Socket conexao = null;
//         ObjectOutputStream transmissor = null;
//         ObjectInputStream receptor = null;
//         Parceiro servidor = null;
//         try
//         {
//             conexao = new Socket(HOST_PADRAO, PORTA_PADRAO);
//             transmissor = new ObjectOutputStream(conexao.getOutputStream());
//             receptor = new ObjectInputStream(conexao.getInputStream());
//             servidor = new Parceiro(conexao, receptor, transmissor);
//         }
//         catch (Exception erro) { System.out.println("Indique o servidor e a porta corretos!"); }
//     }
// }