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

    private static final byte tamanho = 92;

    // Criacao dos jogadores
    private static JLabel player1 = null;
    private static JLabel player2 = null;


    // Posicoes iniciais e direcao jogador 1
    private static int player1x = 200;
    private static int player1y = 200;
    private static char dirPlayer1 = 'N';


    // Posicoes iniciais e direcao jogador 2
    private static int player2x = 425;
    private static int player2y = 425;
    private static char dirPlayer2 = 'S';

    // imagens dos jogadores
    private static ImageIcon imgPlayer1 = null;
    private static ImageIcon imgPlayer2 = null;


    // Quando os jogadores vao se mexer
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
        try { servidor.receba(new Movimentacao(playerMovimentante, direcaoMovimento)); }
        catch (Exception erro) {}
    }


    public static void mandarRotacao(char playerRotante, char direcaoRotacao)
    {
        //Janela.MostrarMensagemDeErro(playerRotante+":"+direcaoPlayerControlante+":"+direcaoRotacao);

        try { servidor.receba(new Rotacao(playerRotante, direcaoRotacao)); }
        catch (Exception erro)
        {
            erro.printStackTrace();
        }
    }


    public static void mandarAtaque(char playerAtacante, char direcaoAtaque)
    {
        try { servidor.receba(new Ataque(playerAtacante, direcaoAtaque)); }
        catch (Exception erro) {}
    }


    public static void setPlayer(int index) throws Exception
    {
        if (index == 0)      { playerControlante = 'A'; direcaoPlayerControlante = 'N'; }
        else if (index == 1) { playerControlante = 'L'; direcaoPlayerControlante = 'S';}
        else throw new Exception("Index out of range");
    } 

    public static void realizarMovimentacao(char playerMovimentante, char direcaoMovimento)
    {
        //Cliente.Janela.MostrarMensagemDeErro("RECEBEU MOVIMENTACAO DE " + playerMovimentante);
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
        //Cliente.Janela.MostrarMensagemDeErro("RECEBEU ROTACAO DE " + playerRotante);
        if (direcaoRotacao == 'N')
        {
            if (playerRotante == 'A')
            {
                dirPlayer1 = 'N';
                player1y -= ESCALA_MOVIMENTACAO / 2;
                player1.setIcon(new ImageIcon(Objects.requireNonNull(Janela.class.getResource("Imagens/player_1_N.png"))));
            }
            if (playerRotante == 'L')
            {
                dirPlayer2 = 'N';
                player2y -= ESCALA_MOVIMENTACAO / 2;
                player2.setIcon(new ImageIcon(Objects.requireNonNull(Janela.class.getResource("Imagens/player_2_N.png"))));
            }
        }
        else if (direcaoRotacao == 'S')
        {
            if (playerRotante == 'A')
            {
                dirPlayer1 = 'S';
                player1y += ESCALA_MOVIMENTACAO / 2;
                player1.setIcon(new ImageIcon(Objects.requireNonNull(Janela.class.getResource("Imagens/player_1_S.png"))));
            }
            if (playerRotante == 'L')
            {
                dirPlayer2 = 'S';
                player2y += ESCALA_MOVIMENTACAO / 2;
                player2.setIcon(new ImageIcon(Objects.requireNonNull(Janela.class.getResource("Imagens/player_2_S.png"))));
            }
        }
        else if (direcaoRotacao == 'O')
        {
            if (playerRotante == 'A')
            {
                dirPlayer1 = 'O';
                player1x -= ESCALA_MOVIMENTACAO / 2;
                player1.setIcon(new ImageIcon(Objects.requireNonNull(Janela.class.getResource("Imagens/player_1_O.png"))));
            }
            if (playerRotante == 'L')
            {
                dirPlayer2 = 'O';
                player2x -= ESCALA_MOVIMENTACAO / 2;
                player2.setIcon(new ImageIcon(Objects.requireNonNull(Janela.class.getResource("Imagens/player_2_E.png"))));
            }
        }
        else if (direcaoRotacao == 'L')
        {
            if (playerRotante == 'A')
            {
                dirPlayer1 = 'L';
                player1x += ESCALA_MOVIMENTACAO / 2;
                player1.setIcon(new ImageIcon(Objects.requireNonNull(Janela.class.getResource("Imagens/player_1_L.png"))));
            }
            if (playerRotante == 'L')
            {
                dirPlayer2 = 'L';
                player2x += ESCALA_MOVIMENTACAO / 2;
                player2.setIcon(new ImageIcon(Objects.requireNonNull(Janela.class.getResource("Imagens/player_2_L.png"))));
            }
        }

        if (playerControlante == playerRotante)
            direcaoPlayerControlante = direcaoRotacao;

        Janela.AtualizarTela();
    }


    public static void realizarAtaque(char playerAtacante, char direcaoAtaque)
    {
        //Cliente.Janela.MostrarMensagemDeErro(playerAtacante + " ATACOU");
        char playerAtacado = 'L';
        if (playerAtacante == 'L')
            playerAtacado = 'A';

        if (direcaoAtaque == 'N')
        {
            if ((player1x + tamanho/4) >= player2x && (player1x + 3*tamanho/4) >= player2x)
            {
                if (Math.abs(player2y - player1y) <= tamanho * 1.25)
                    realizarMovimentacao(playerAtacado, 'N');
            }
        }
        else if (direcaoAtaque == 'S')
        {
            if ((player1x + tamanho/4) >= player2x && (player1x + 3*tamanho/4) >= player2x)
            {
                if (Math.abs(player1y - player2y) <= tamanho * 1.25)
                    realizarMovimentacao(playerAtacado, 'S');
            }
        }
        else if (direcaoAtaque == 'L')
        {
            if ((player1y + tamanho/4) >= player2y && (player1y + 3*tamanho/4) >= player2y)
            {
                if (Math.abs(player1x - player2x) <= tamanho * 1.25)
                    realizarMovimentacao(playerAtacado, 'L');
            }
        }
        else if (direcaoAtaque == 'O')
        {
            if ((player1y + tamanho/4) >= player2y && (player1y + 3*tamanho/4) >= player2y)
            {
                if (Math.abs(player2x - player1x) <= tamanho * 1.25)
                    realizarMovimentacao(playerAtacado, '0');
            }
        }

        try
        {
            Cliente.moverBastao(playerAtacante, direcaoAtaque);
        }
        catch (Exception erro) {}
    }

    public static void moverBastao (char playerAtacante, char direcaoAtaque) throws InterruptedException
    {
        int delay = 50;
        if (playerAtacante == 'A')
        {
            if (direcaoAtaque == 'N')
            {
                player1.setIcon(new ImageIcon(Objects.requireNonNull(Janela.class.getResource("Imagens/player_1_N_ataque.png"))));
                Janela.AtualizarTela();
                Thread.sleep(delay);
                player1.setIcon(new ImageIcon(Objects.requireNonNull(Janela.class.getResource("Imagens/player_1_N.png"))));
            }
            if (direcaoAtaque == 'S')
            {
                player1.setIcon(new ImageIcon(Objects.requireNonNull(Janela.class.getResource("Imagens/player_1_S_ataque.png"))));
                Janela.AtualizarTela();
                Thread.sleep(delay);
                player1.setIcon(new ImageIcon(Objects.requireNonNull(Janela.class.getResource("Imagens/player_1_S.png"))));
            }
            if (direcaoAtaque == 'L')
            {
                player1.setIcon(new ImageIcon(Objects.requireNonNull(Janela.class.getResource("Imagens/player_1_L_ataque.png"))));
                Janela.AtualizarTela();
                Thread.sleep(delay);
                player1.setIcon(new ImageIcon(Objects.requireNonNull(Janela.class.getResource("Imagens/player_1_L.png"))));
            }
            if (direcaoAtaque == 'O')
            {
                player1.setIcon(new ImageIcon(Objects.requireNonNull(Janela.class.getResource("Imagens/player_1_O_ataque.png"))));
                Janela.AtualizarTela();
                Thread.sleep(delay);
                player1.setIcon(new ImageIcon(Objects.requireNonNull(Janela.class.getResource("Imagens/player_1_O.png"))));
            }
        }
        else if (playerAtacante == 'L')
        {
            if (direcaoAtaque == 'N')
            {
                player2.setIcon(new ImageIcon(Objects.requireNonNull(Janela.class.getResource("Imagens/player_2_N_ataque.png"))));
                Janela.AtualizarTela();
                Thread.sleep(delay);
                player2.setIcon(new ImageIcon(Objects.requireNonNull(Janela.class.getResource("Imagens/player_2_N.png"))));
            }
            if (direcaoAtaque == 'S')
            {
                player2.setIcon(new ImageIcon(Objects.requireNonNull(Janela.class.getResource("Imagens/player_2_S_ataque.png"))));
                Janela.AtualizarTela();
                Thread.sleep(delay);
                player2.setIcon(new ImageIcon(Objects.requireNonNull(Janela.class.getResource("Imagens/player_2_S.png"))));
            }
            if (direcaoAtaque == 'L')
            {
                player2.setIcon(new ImageIcon(Objects.requireNonNull(Janela.class.getResource("Imagens/player_2_L_ataque.png"))));
                Janela.AtualizarTela();
                Thread.sleep(delay);
                player2.setIcon(new ImageIcon(Objects.requireNonNull(Janela.class.getResource("Imagens/player_2_L.png"))));
            }
            if (direcaoAtaque == 'O')
            {
                player2.setIcon(new ImageIcon(Objects.requireNonNull(Janela.class.getResource("Imagens/player_2_O_ataque.png"))));
                Janela.AtualizarTela();
                Thread.sleep(delay);
                player2.setIcon(new ImageIcon(Objects.requireNonNull(Janela.class.getResource("Imagens/player_2_O.png"))));
            }
        }
    }


    public static class Janela extends JFrame
    {   
        static JLayeredPane fundo;
        static JLabel resultado = new JLabel("");

        public Janela () 
        {
            super("SumoBalls");
            
            this.setLayout(null);
            
            JLabel titulo = new JLabel("SumoBalls");
            titulo.setBounds(225, 30, 300, 50);
            titulo.setForeground(Color.ORANGE);
            titulo.setFont(new Font("Monospace", Font.BOLD, 50));


            resultado.setBounds(210, 275, 400, 75);
            resultado.setFont(new Font("Monospace", Font.BOLD, 75));
            resultado.setVisible(false);


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
            fundo.add(resultado,0);
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

        public static void comunicarVitoria(char playerVencedor, boolean desistencia)
        {
            if (playerVencedor == playerControlante)
            {
                if (desistencia) {
                    resultado.setText("Ganhou por desistência!");
                    resultado.setForeground(Color.GREEN);
                }
                else {
                    resultado.setText("Ganhou!");
                    resultado.setForeground(Color.GREEN);
                }
            }
            else
            {
                resultado.setText("Perdeu!");
                resultado.setForeground(Color.RED);
            }

            resultado.setVisible(true);

        }

        public static void AtualizarTela()
        {
            player1.setBounds(player1x, player1y, 92, 92);
            player2.setBounds(player2x, player2y, 92, 92);

            try
            {
                if (player1x <= 58 || player1x >= 550 || player1y >= 550 || player1y <= 58)
                    servidor.receba(new ComunicadoDeVitoria('L', false));

                if (player2x <= 58 || player2x >= 550 || player2y >= 550 || player2y <= 58)
                    servidor.receba(new ComunicadoDeVitoria('A', false));
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
                try
                {
                    if (playerControlante == 'A')
                        servidor.receba(new ComunicadoDeVitoria('L', true));
                    else
                        servidor.receba(new ComunicadoDeVitoria('A', true));
                    servidor.receba(new PedidoParaSair());
                }
                catch (Exception erro) {
                    erro.printStackTrace();
                }
                System.exit(0);
            }
        }

        static class keyHandler extends KeyAdapter  {
            public void keyPressed(KeyEvent e) {
                try
                {
                    if (e.getKeyCode() == KeyEvent.VK_W) {
                        if (direcaoPlayerControlante == 'S' || direcaoPlayerControlante == 'O' || direcaoPlayerControlante == 'L') {
                            Cliente.mandarRotacao(playerControlante, 'N');
                            //Cliente.Janela.MostrarMensagemDeErro("ROTACAO");
                        } else
                            Cliente.mandarMovimentacao(playerControlante, 'N');
                    } else if (e.getKeyCode() == KeyEvent.VK_A) {
                        if (direcaoPlayerControlante == 'N' || direcaoPlayerControlante == 'S' || direcaoPlayerControlante == 'L')
                            Cliente.mandarRotacao(playerControlante, 'O');
                        else
                            Cliente.mandarMovimentacao(playerControlante, 'O');
                    } else if (e.getKeyCode() == KeyEvent.VK_S) {
                        if (direcaoPlayerControlante == 'N' || direcaoPlayerControlante == 'O' || direcaoPlayerControlante == 'L')
                            Cliente.mandarRotacao(playerControlante, 'S');
                        else
                            Cliente.mandarMovimentacao(playerControlante, 'S');
                    } else if (e.getKeyCode() == KeyEvent.VK_D) {
                        if (direcaoPlayerControlante == 'S' || direcaoPlayerControlante == 'O' || direcaoPlayerControlante == 'N')
                            Cliente.mandarRotacao(playerControlante, 'L');
                        else
                            Cliente.mandarMovimentacao(playerControlante, 'L');
                    } else if (e.getKeyCode() == KeyEvent.VK_L) {
                        Cliente.mandarAtaque(playerControlante, direcaoPlayerControlante);
                    }
                }
                catch (Exception erro)
                {
                    erro.printStackTrace();
                }
            }

        }
    }
}