import java.net.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import javax.swing.*;

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
    private static int player1x = 425;
    private static int player1y = 425;
    private static char dirPlayer1 = 'N';


    // Posicoes iniciais e direcao jogador 2
    private static int player2x = 200;
    private static int player2y = 200;
    private static char dirPlayer2 = 'S';

    // imagens dos jogadores
    private static ImageIcon imgPlayer1 = null;
    private static ImageIcon imgPlayer2 = null;


    // Quanto os jogadores vao se mexer
    private static final int ESCALA_MOVIMENTACAO = 20;

    private static char playerControlante = 'A';
    private static char direcaoPlayerControlante = 'N';

    static Janela janela = null;

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

        janela = new Janela();
    }


    public static void mandarMovimentacao(char playerMovimentante, char direcaoMovimento)
    {
        //Janela.MostrarMensagemDeErro(playerMovimentante + " MOVEU DE " + direcaoPlayerControlante + " PARA " + direcaoMovimento);
        try { servidor.receba(new Movimentacao(playerMovimentante, direcaoMovimento)); }
        catch (Exception erro) {Janela.MostrarMensagemDeErro(erro.getMessage());}
    }


    public static void mandarRotacao(char playerRotante, char direcaoRotacao)
    {
        //Janela.MostrarMensagemDeErro(playerRotante + " ROTACIONOU DE " + direcaoPlayerControlante + " PARA " + direcaoRotacao);
        try { servidor.receba(new Rotacao(playerRotante, direcaoRotacao)); }
        catch (Exception erro) {Janela.MostrarMensagemDeErro(erro.getMessage());}
    }


    public static void mandarAtaque(char playerAtacante, char direcaoAtaque)
    {
        //Janela.MostrarMensagemDeErro(playerAtacante + " ATACOU PARA O " + direcaoAtaque);
        try { servidor.receba(new Ataque(playerAtacante, direcaoAtaque)); }
        catch (Exception erro) {Janela.MostrarMensagemDeErro(erro.getMessage());}
    }


    public static void setPlayer(int index) throws Exception
    {
        Janela.MostrarMensagemDeErro("INDEX: " + index);
        if (index == 0)      { playerControlante = 'A'; direcaoPlayerControlante = 'N'; }
        else if (index == 1) { playerControlante = 'L'; direcaoPlayerControlante = 'S';}
        else throw new Exception("Index out of range");
        Janela.MostrarMensagemDeErro(playerControlante+"");
        Janela.MostrarMensagemDeErro(direcaoPlayerControlante+"");
    } 

    public static void realizarMovimentacao(char playerMovimentante, char direcaoMovimento)
    {
        Cliente.Janela.MostrarMensagemDeErro("UMA MOVIMENTACAO ESTA ACONTECENDO");
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
    {
        Cliente.Janela.MostrarMensagemDeErro("UMA ROTACAO ESTA ACONTECENDO");
        if (direcaoRotacao == 'N')
        {
            if (playerRotante == 'A')
            {
                dirPlayer1 = 'N';
            }
            if (playerRotante == 'L')
            {
                dirPlayer1 = 'N';
            }
        }
        else if (direcaoRotacao == 'S')
        {
            if (playerRotante == 'A')
            {
                dirPlayer1 = 'S';
            }
            if (playerRotante == 'L')
            {
                dirPlayer1 = 'S';
            }
        }
        else if (direcaoRotacao == 'O')
        {
            if (playerRotante == 'A')
            {
                dirPlayer1 = 'O';
            }
            if (playerRotante == 'L')
            {
                dirPlayer1 = 'O';
            }
        }
        else if (direcaoRotacao == 'L')
        {
            if (playerRotante == 'A')
            {
                dirPlayer1 = 'L';
            }
            if (playerRotante == 'L')
            {
                dirPlayer1 = 'L';
            }
        }

        if (playerControlante == playerRotante)
            direcaoPlayerControlante = direcaoRotacao;

        Janela.AtualizarTela();
    }


    public static void realizarAtaque(char playerAtacante, char direcaoAtaque)
    {
        Cliente.Janela.MostrarMensagemDeErro("UM ATAQUE ESTA ACONTECENDO");
    }



    public static class Janela extends JFrame
    {
        static JLayeredPane fundo;

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


            imgPlayer1 = new ImageIcon(Objects.requireNonNull(getClass().getResource("Imagens/player_1_N.png")));
            player1 = new JLabel(imgPlayer1);
            player1.setBounds(player1x, player1y, 92, 92);


            imgPlayer2 = new ImageIcon(Objects.requireNonNull(getClass().getResource("Imagens/player_2_S.png")));
            player2 = new JLabel(imgPlayer2);
            player2.setBounds(player2x, player2y, 92, 92);

            fundo = new JLayeredPane();
            fundo.setSize(600, 600);
            fundo.add(titulo, 2);
            fundo.add(ringue, 2);
            fundo.add(player1, 1);
            fundo.add(player2, 1);

            Container cntForm = this.getContentPane();
            cntForm.add(fundo);
            cntForm.setBackground(Color.DARK_GRAY);

            this.addWindowListener(new FechamentoDeJanela());
            this.addKeyListener(new keyHandler());
            this.setSize(700, 700);
            this.setVisible(true);
            this.setResizable(false);
        }

        public static void AtualizarTela()
        {
            player1.setBounds(player1x, player1y, 92, 92);
            player2.setBounds(player2x, player2y, 92, 92);
            player1 = new JLabel(imgPlayer1);
            player2 = new JLabel(imgPlayer2);

            try
            {
                if (player1x <= 92 || player1x >= 550 || player1y >= 550 || player1y <= 92)
                    servidor.receba(new ComunicadoDeVitoria('L'));

                if (player2x <= 92 || player2x >= 550 || player2y >= 550 || player2y <= 92)
                    servidor.receba(new ComunicadoDeVitoria('A'));
            }
            catch (Exception erro)
            {
                MostrarMensagemDeErro(erro.getMessage());
            }
        }

        public static void MostrarMensagemDeErro(String erroRecebido)
        {
            JOptionPane.showMessageDialog(fundo, erroRecebido, "Um erro aconteceu", JOptionPane.ERROR_MESSAGE);
        }


        static class FechamentoDeJanela extends WindowAdapter {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        }

        static class keyHandler implements KeyListener  {
            @Override
            public void keyReleased(KeyEvent e)
            {
                if (e.getKeyCode() == KeyEvent.VK_W) {
                    if (direcaoPlayerControlante == 'S' || direcaoPlayerControlante == 'O' || direcaoPlayerControlante == 'L')
                    {
                        //Janela.MostrarMensagemDeErro("Rotacionou");
                        Cliente.mandarRotacao(playerControlante, 'N');
                    }
                    else {
                        //Janela.MostrarMensagemDeErro("Movimentou");
                        Cliente.mandarMovimentacao(playerControlante, 'N');
                    }
                }
                else if (e.getKeyCode() == KeyEvent.VK_A)
                {
                    if (direcaoPlayerControlante == 'N' || direcaoPlayerControlante == 'S' || direcaoPlayerControlante == 'L') {
                        //Janela.MostrarMensagemDeErro("Rotacionou");
                        Cliente.mandarRotacao(playerControlante, 'O');
                    }
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

            @Override
            public void keyPressed(KeyEvent e) { }
            @Override
            public void keyTyped(KeyEvent e) { }
        }
    }
}