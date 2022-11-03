import java.net.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
//import java.util.*;
//import javax.swing.*;

public class Cliente
{
    // Parte do Socket
    public static final String HOST_PADRAO = "localhost";
    public static final int PORTA_PADRAO = 3000;
    private static Parceiro servidor = null; 


    // Criacao dos jogadores
    private static JLabel player1 = null;
    private static JLabel player2 = null;


    // Posicoes iniciais e direcao jogador 1
    private static int player1x = 200;
    private static int player1y = 200;
    private static char dirPlayer1 = 'N';
    private static ImageIcon imgPlayer1;


    // Posicoes iniciais e direcao jogador 2
    private static int player2x = 425;
    private static int player2y = 425;
    private static char dirPlayer2 = 'S';
    private static ImageIcon imgPlayer2;


    // Quando os jogadores vao se mexer
    private static final int ESCALA_MOVIMENTACAO = 10;

    private static char playerControlante;
    private static char direcaoPlayerControlante;


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
        TratadoraJogador tratadoraJogador = null;

        try
        {
            tratadoraDeComunicadoDeDesligamento = new TratadoraDeComunicadoDeDesligamento(servidor);
            tratadoraJogador = new TratadoraJogador(servidor);
        }
        catch (Exception erro)
        {}

        tratadoraDeComunicadoDeDesligamento.start();
        tratadoraJogador.start();

        new Janela();
    }


    public static void mandarMovimentacao(char playerMovimentante, char direcaoMovimento)
    {
        try { servidor.receba(new Movimentacao(playerMovimentante, direcaoMovimento)); }
        catch (Exception erro) {}
    }


    public static void mandarRotacao(char playerRotante, char direcaoRotacao)
    {
        try { servidor.receba(new Rotacao(playerRotante, direcaoRotacao)); }
        catch (Exception erro) {}
    }


    public static void mandarAtaque(char playerAtacante, char direcaoAtaque)
    {
        try { servidor.receba(new Ataque(playerAtacante, direcaoAtaque)); }
        catch (Exception erro) {}
    }


    public static void setPlayer(int index) throws Exception
    {
        if (index == 0)      { Janela.setEventListener(player1); playerControlante = 'A'; direcaoPlayerControlante = 'N'; }
        else if (index == 1) { Janela.setEventListener(player2); playerControlante = 'L'; direcaoPlayerControlante = 'S';}
        else throw new Exception("Index out of range");
    } 

    public static void realizarMovimentacao(char playerMovimentante, char direcaoMovimento)
    {
        if (direcaoMovimento == 'N')
        {
            if (playerMovimentante == 'A') player1y -= ESCALA_MOVIMENTACAO;
            if (playerMovimentante == 'L') player2y -= ESCALA_MOVIMENTACAO;
        }
        else if (direcaoMovimento == 'O')
        {
            if (playerMovimentante == 'A') player1x -= ESCALA_MOVIMENTACAO;
            if (playerMovimentante == 'L') player2x -= ESCALA_MOVIMENTACAO;
        }
        else if (direcaoMovimento == 'S')
        {
            if (playerMovimentante == 'A') player1y += ESCALA_MOVIMENTACAO;
            if (playerMovimentante == 'L') player2y += ESCALA_MOVIMENTACAO;
        }
        else if (direcaoMovimento == 'L')
        {
            if (playerMovimentante == 'A') player1x += ESCALA_MOVIMENTACAO;
            if (playerMovimentante == 'L') player2x += ESCALA_MOVIMENTACAO;
        }

        Janela.AtualizarTela();
    }


    public static void realizarRotacao(char playerRotante, char direcaoRotacao)
    { // a fazer
        /*if (playerRotante == 'A')
        {
            if (direcaoRotacao == 'N')
            {
                dirPlayer1 = direcaoRotacao;
                imgPlayer1 = new ImageIcon(getClass().getResource("NOVA IMAGEM"));
                player1 = new JLabel(imgPlayer1);
                player1.setBounds(player1x, player1y, 75, 75);
            }
        }*/
    }


    public static void realizarAtaque(char playerAtacante, char direcaoAtaque)
    {

    }


    public static class Janela extends JFrame
    {   
        protected JLayeredPane fundo;
        static Container cntForm = null; 

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

            
            imgPlayer1 = new ImageIcon(getClass().getResource("Imagens/player_1.png"));
            player1 = new JLabel(imgPlayer1);
            player1.setBounds(player1x, player1y, 75, 75);

            
            imgPlayer2 = new ImageIcon(getClass().getResource("Imagens/player_2.png"));
            player2 = new JLabel(imgPlayer2);
            player2.setBounds(player2x, player2y, 75, 75);

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
            this.setSize(700, 700);
            this.setVisible(true);
            this.setResizable(false);
        }

        public static void AtualizarTela()
        {
            player1.setBounds(player1x, player1y, 75, 75);
            player2.setBounds(player2x, player2y, 75, 75);

            try
            {
                if (player1x <= 75 || player1x >= 550 || player1y >= 550 || player1y <= 75)
                    servidor.receba(new ComunicadoDeVitoria('L'));

                if (player2x <= 75 || player2x >= 550 || player2y >= 550 || player2y <= 75)
                    servidor.receba(new ComunicadoDeVitoria('A'));
            }
            catch (Exception erro)
            {
                MostrarMensagemDeErro(erro.getMessage());
            }
        }

        public static void MostrarMensagemDeErro(String erroRecebido)
        {
            JOptionPane.showMessageDialog(cntForm, erroRecebido, "Um erro aconteceu", JOptionPane.ERROR_MESSAGE);
        }

        public static void setEventListener(JLabel player)
        { // I dont know
            //Janela.keyEvent k = new keyEvent();
           // player.addKeyListener(k);
            //player.addKeyListener(new keyEvent());
        }

        class FechamentoDeJanela extends WindowAdapter {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        }

        class keyEvent implements KeyListener {
            public void keyTyped(KeyEvent e) { }
        
            public void keyPressed(KeyEvent e) 
            {
                if (e.getKeyCode() == KeyEvent.VK_W) 
                { 
                    if (direcaoPlayerControlante == 'S' || direcaoPlayerControlante == 'O' || direcaoPlayerControlante == 'L')
                        Cliente.mandarRotacao(playerControlante,'N');
                    else
                        Cliente.mandarMovimentacao(playerControlante,'N');
                }
                else if (e.getKeyCode() == KeyEvent.VK_A) 
                { 
                    if (direcaoPlayerControlante == 'N' || direcaoPlayerControlante == 'S' || direcaoPlayerControlante == 'L')
                        Cliente.mandarRotacao(playerControlante,'O');
                    else
                        Cliente.mandarMovimentacao(playerControlante,'O');
                }
                else if (e.getKeyCode() == KeyEvent.VK_S) 
                { 
                   if (direcaoPlayerControlante == 'N' || direcaoPlayerControlante == 'O' || direcaoPlayerControlante == 'L')
                        Cliente.mandarRotacao(playerControlante,'S');
                    else
                        Cliente.mandarMovimentacao(playerControlante,'S');
                }
                else if (e.getKeyCode() == KeyEvent.VK_D) 
                { 
                    if (direcaoPlayerControlante == 'S' || direcaoPlayerControlante == 'O' || direcaoPlayerControlante == 'N')
                        Cliente.mandarRotacao(playerControlante,'L');
                    else
                        Cliente.mandarMovimentacao(playerControlante,'L');
                }
                else if (e.getKeyCode() == KeyEvent.VK_L) 
                { 
                    Cliente.mandarAtaque(playerControlante,direcaoPlayerControlante);
                }
            }
        
            public void keyReleased(KeyEvent e) { }
        }
    }
}