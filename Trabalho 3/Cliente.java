import java.net.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import javax.swing.*;

public class Cliente 
{
    public static final String HOST_PADRAO = "localhost";
    public static final int PORTA_PADRAO = 3000;
    public static void main (String args[])
    {
        if (args.length > 2)
        {
            System.err.println("Uso esperado: java Cliente [HOST [PORTA]]\n");
            return;
        }

        Socket conexao = null;
        ObjectOutputStream transmissor = null;
        ObjectInputStream receptor = null;
        Parceiro servidor = null;
        try
        {
            String host = Cliente.HOST_PADRAO;
            int   porta = Cliente.PORTA_PADRAO;

            if (args.length > 0)
                host = args[0];
            
            if (args.length == 2)
                porta = Integer.parseInt(args[1]);

            conexao = new Socket(host, porta);
            transmissor = new ObjectOutputStream(conexao.getOutputStream());
            receptor = new ObjectInputStream(conexao.getInputStream());
            servidor = new Parceiro(conexao, receptor, transmissor);
        }
        catch (Exception erro) { System.out.println("Indique o servidor e a porta corretos!"); }

        TratadoraDeComunicadoDeDesligamento tratadoraDeComunicadoDeDesligamento = null;

        try
        {
            tratadoraDeComunicadoDeDesligamento = new TratadoraDeComunicadoDeDesligamento(servidor);
        }
        catch (Exception erro)
        {}

        tratadoraDeComunicadoDeDesligamento.start();

        new Janela();
    }

    public static class Janela extends JFrame
    {   
        protected JLayeredPane fundo;

        static Container cntForm = null;
        static JLabel player1 = null;
        static JLabel player2 = null;

        static int player1x = 200;
        static int player1y = 200;


        static int player2x = 425;
        static int player2y = 425;


        public Janela () 
        {
            super("SumoBalls");
            
            this.setLayout(null);
            
            JLabel titulo = new JLabel("SumoBalls");
            titulo.setBounds(225, 30, 300, 50);
            titulo.setForeground(Color.ORANGE);
            titulo.setFont(new Font("Monospace", Font.BOLD, 50));

            JPanel ringue = new JPanel();
            ringue.setBounds(150, 150, 400, 400);
            ringue.setBackground(Color.LIGHT_GRAY);
            ringue.setBorder(BorderFactory.createLineBorder(Color.BLUE, 7));

            
            ImageIcon imgPlayer1 = new ImageIcon(getClass().getResource("Imagens/player_1.png"));
            player1 = new JLabel(imgPlayer1);
            player1.setBounds(Janela.player1x, Janela.player1y, 75, 75);

            
            ImageIcon imgPlayer2 = new ImageIcon(getClass().getResource("Imagens/player_2.png"));
            player2 = new JLabel(imgPlayer2);
            player2.setBounds(Janela.player2x, Janela.player2y, 75, 75);

            this.fundo = new JLayeredPane();
            this.fundo.setSize(600, 600);
            this.fundo.add(titulo, 2);
            this.fundo.add(ringue, 2);
            this.fundo.add(player1, 1);
            this.fundo.add(player2, 1);

            cntForm = this.getContentPane();
            cntForm.add(this.fundo);
            cntForm.setBackground(Color.DARK_GRAY);

            this.addWindowListener(new FechamentoDeJanela());
            this.addKeyListener(new keyEvent());
            this.setSize(700, 700);
            this.setVisible(true);
            this.setResizable(false);
        }

        public static void MostrarMensagemDeErro(String erroRecebido)
        {
            JOptionPane.showMessageDialog(cntForm, erroRecebido, "Um erro aconteceu", JOptionPane.ERROR_MESSAGE);
        }

        class FechamentoDeJanela extends WindowAdapter 
        {
            public void windowClosing(WindowEvent e) 
            {
                System.exit(0);
            }
        }

        class keyEvent implements KeyListener 
        {
            public void keyTyped(KeyEvent e)
            {
            }
        
            public void keyPressed(KeyEvent e) 
            {
                if (e.getKeyCode() == KeyEvent.VK_W) 
                {
                    Janela.player1y -= 30;
                }
                else if (e.getKeyCode() == KeyEvent.VK_A)
                {
                    Janela.player1x -= 30;
                }
                else if (e.getKeyCode() == KeyEvent.VK_S)
                {
                    Janela.player1y += 30;
                }
                else if (e.getKeyCode() == KeyEvent.VK_D)
                {
                    Janela.player1x += 30;
                }

                Janela.player1.setBounds(Janela.player1x, Janela.player1y, 75, 75);
            }
        
            public void keyReleased(KeyEvent e) 
            {
            }
        }
    }
    
}