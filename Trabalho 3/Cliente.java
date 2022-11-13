import java.net.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

public class Cliente
{
    // Parte do Socket
    //public static final String HOST_PADRAO = "localhost";
    public static final int PORTA_PADRAO = 3000;
    private static Parceiro servidor = null;

    static String host;
    public static boolean isPassed;

    private static final byte tamanho = 92;

    // Criacao dos jogadores
    private static JLabel player1 = null;
    private static JLabel player2 = null;


    // Posicoes iniciais e direcao jogador 1
    private static int player1x = 200;
    private static int player1y = 200;


    // Posicoes iniciais e direcao jogador 2
    private static int player2x = 425;
    private static int player2y = 425;

    // imagens dos jogadores
    private static ImageIcon imgPlayer1 = null;


    // Quando os jogadores vao se mexer
    private static int ESCALA_MOVIMENTACAO = 20;

    private static char playerControlante;
    private static char direcaoPlayerControlante;

    private static boolean isFinished = false;

    static Janela janela = null;

    static Cliente.Janela.keyHandler kh = new Janela.keyHandler();


    public static void main (String args[]) {
        ObterHost oh = new ObterHost();

        do {
            try {
                Thread.sleep(1000);
            } catch (Exception e) {}
        }
        while (!isPassed);

        if (args.length > 2) {
            System.err.println("Uso esperado: java Cliente [HOST [PORTA]]\n");
            return;
        }

        Socket conexao = null;
        ObjectOutputStream transmissor = null;
        ObjectInputStream receptor = null;

        try {
            int porta = Cliente.PORTA_PADRAO;

            if (args.length > 0)
                host = args[0];

            if (args.length == 2)
                porta = Integer.parseInt(args[1]);

            conexao = new Socket(host, porta);
            transmissor = new ObjectOutputStream(conexao.getOutputStream());
            receptor = new ObjectInputStream(conexao.getInputStream());
            servidor = new Parceiro(conexao, receptor, transmissor);
        }
        catch (Exception erro) {
            ObterHost.MostrarMensagemDeErro("Indique o servidor e a porta corretos!");
            System.exit(0);
        }

        TratadoraDeComunicadoDeDesligamento tratadoraDeComunicadoDeDesligamento = null;
        TratadoraJogador tratadoraJogador = null;

        try {
            tratadoraDeComunicadoDeDesligamento = new TratadoraDeComunicadoDeDesligamento(servidor);
            tratadoraJogador = new TratadoraJogador(servidor);
        }
        catch (Exception erro) {
        }

        tratadoraDeComunicadoDeDesligamento.start();
        tratadoraJogador.start();

        janela = new Janela();
        oh.getObterHost().dispose();
    }


    public static void mandarMovimentacao(char playerMovimentante, char direcaoMovimento)
    {
        try { servidor.receba(new Movimentacao(playerMovimentante, direcaoMovimento)); }
        catch (Exception erro) {}
    }

    public static void mandarRotacao(char playerRotante, char direcaoRotacao)
    {
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
        if (index == 0) { playerControlante = 'A'; direcaoPlayerControlante = 'N'; }
        else if (index == 1) {
            playerControlante = 'L'; direcaoPlayerControlante = 'S';
            servidor.receba(new ComunicadoInicio());
        }
        else
        {
            Cliente.Janela.MostrarMensagemDeErro("Essa partida já começou. Volte a tentar mais tarde!");
            try { servidor.receba(new PedidoParaSair()); }
            catch (Exception erro) { };
            System.exit(0);
        }
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
    {
        char dirPlayer1 = 'N';
        char dirPlayer2 = 'S';
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
                player2.setIcon(new ImageIcon(Objects.requireNonNull(Janela.class.getResource("Imagens/player_2_O.png"))));
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
        ESCALA_MOVIMENTACAO = 40;

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
                    realizarMovimentacao(playerAtacado, 'O');
            }
        }

        try
        {
            Cliente.moverBastao(playerAtacante, direcaoAtaque);
        }
        catch (Exception erro) {}

        ESCALA_MOVIMENTACAO = 20;
    }

    public static void moverBastao (char playerAtacante, char direcaoAtaque) throws InterruptedException
    {
        int delay = 50;
        if (playerAtacante == 'A')
        {
            if (direcaoAtaque == 'N')
            {
                player1.setIcon(new ImageIcon(Objects.requireNonNull(Janela.class.getResource("Imagens/player_1_N_ataque.png"))));
                Thread.sleep(delay);
                player1.setIcon(new ImageIcon(Objects.requireNonNull(Janela.class.getResource("Imagens/player_1_N.png"))));
            }
            if (direcaoAtaque == 'S')
            {
                player1.setIcon(new ImageIcon(Objects.requireNonNull(Janela.class.getResource("Imagens/player_1_S_ataque.png"))));
                Thread.sleep(delay);
                player1.setIcon(new ImageIcon(Objects.requireNonNull(Janela.class.getResource("Imagens/player_1_S.png"))));
            }
            if (direcaoAtaque == 'L')
            {
                player1.setIcon(new ImageIcon(Objects.requireNonNull(Janela.class.getResource("Imagens/player_1_L_ataque.png"))));
                Thread.sleep(delay);
                player1.setIcon(new ImageIcon(Objects.requireNonNull(Janela.class.getResource("Imagens/player_1_L.png"))));
            }
            if (direcaoAtaque == 'O')
            {
                player1.setIcon(new ImageIcon(Objects.requireNonNull(Janela.class.getResource("Imagens/player_1_O_ataque.png"))));
                Thread.sleep(delay);
                player1.setIcon(new ImageIcon(Objects.requireNonNull(Janela.class.getResource("Imagens/player_1_O.png"))));
            }
        }
        else if (playerAtacante == 'L')
        {
            if (direcaoAtaque == 'N')
            {
                player2.setIcon(new ImageIcon(Objects.requireNonNull(Janela.class.getResource("Imagens/player_2_N_ataque.png"))));
                Thread.sleep(delay);
                player2.setIcon(new ImageIcon(Objects.requireNonNull(Janela.class.getResource("Imagens/player_2_N.png"))));
            }
            if (direcaoAtaque == 'S')
            {
                player2.setIcon(new ImageIcon(Objects.requireNonNull(Janela.class.getResource("Imagens/player_2_S_ataque.png"))));
                Thread.sleep(delay);
                player2.setIcon(new ImageIcon(Objects.requireNonNull(Janela.class.getResource("Imagens/player_2_S.png"))));
            }
            if (direcaoAtaque == 'L')
            {
                player2.setIcon(new ImageIcon(Objects.requireNonNull(Janela.class.getResource("Imagens/player_2_L_ataque.png"))));
                Thread.sleep(delay);
                player2.setIcon(new ImageIcon(Objects.requireNonNull(Janela.class.getResource("Imagens/player_2_L.png"))));
            }
            if (direcaoAtaque == 'O')
            {
                player2.setIcon(new ImageIcon(Objects.requireNonNull(Janela.class.getResource("Imagens/player_2_O_ataque.png"))));
                Thread.sleep(delay);
                player2.setIcon(new ImageIcon(Objects.requireNonNull(Janela.class.getResource("Imagens/player_2_O.png"))));
            }
        }
    }

    public static void iniciar() throws Exception
    {
        int delay = 1000;
        try
        {
            Janela.resultado.setBounds(120, 275, 400, 75);
            Janela.resultado.setFont(new Font("Monospace", Font.BOLD, 15));
            Janela.resultado.setText("3...");
            Thread.sleep(delay);

            Janela.resultado.setText("2...");
            Thread.sleep(delay);

            Janela.resultado.setText("1...");
            Thread.sleep(delay);

            Janela.resultado.setBounds(245, 285, 450, 75);
            Janela.resultado.setFont(new Font("Monospace", Font.BOLD, 60));
            Janela.resultado.setText("Lutem!");

            Thread.sleep(delay);
            Janela.resultado.setVisible(false);
            Thread.sleep(delay);

            habilitarEventos();
        }
        catch (Exception erro)
        { }
    }

    public static void habilitarEventos()   { System.out.print("cc"); janela.ObterJanela().addKeyListener(kh); }

    public static void desabilitarEventos() { System.out.print("dd"); janela.ObterJanela().removeKeyListener(kh); }

    public static class ObterHost extends JFrame
    {
        public ObterHost getObterHost() {
            return ObterHost.this;
        }

        static JTextField host = new JTextField();
        static Container cntForm;

        JTextField novoHost;

        public ObterHost ()
        {
            super ("Obter Host");

            isPassed = false;

            JLabel message = new JLabel("Digite o host desejado: ");
            novoHost = new JTextField();
            JButton botao = new JButton("Iniciar jogo");
            botao.addActionListener(new InserirHost());
            cntForm = this.getContentPane();
            cntForm.setLayout(new BorderLayout());
            cntForm.add(message, BorderLayout.NORTH);
            cntForm.add(novoHost, BorderLayout.CENTER);
            cntForm.add(botao, BorderLayout.SOUTH);

            this.addWindowListener(new ObterHost.FechamentoDeJanela());
            this.setSize(400, 100);
            this.setVisible(true);
            this.setResizable(false);
        }

        static class FechamentoDeJanela extends WindowAdapter {
            public void windowClosing(WindowEvent e) { System.exit(0); }
        }

        class InserirHost implements ActionListener {
            public void actionPerformed(ActionEvent e) {
                Cliente.host = novoHost.getText();
                isPassed = true;
            }
        }

        public static void MostrarMensagemDeErro(String erroRecebido)
        {
            JOptionPane.showMessageDialog (cntForm, erroRecebido, "Um erro aconteceu", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static class Janela extends JFrame
    {
        static JLayeredPane fundo;
        static JLabel resultado = new JLabel("Aguardando players");

        public Janela ObterJanela() {
            return Janela.this;
        }

        public Janela ()
        {
            super("SumoBalls");

            this.setLayout(null);

            JLabel titulo = new JLabel("SumoBalls");
            titulo.setBounds(225, 30, 400, 50);
            titulo.setForeground(Color.ORANGE);
            titulo.setFont(new Font("Monospace", Font.BOLD, 50));


            resultado.setBounds(160, 275, 400, 75);
            resultado.setFont(new Font("Monospace", Font.BOLD, 40));
            resultado.setVisible(true);


            JPanel ringue = new JPanel();
            ringue.setBounds(150, 150, 400, 400);
            ringue.setBackground(Color.LIGHT_GRAY);
            ringue.setBorder(BorderFactory.createLineBorder(Color.BLUE, 7));


            imgPlayer1 = new ImageIcon(Objects.requireNonNull(getClass().getResource("Imagens/player_1_N.png")));
            player1 = new JLabel(imgPlayer1);
            player1.setBounds(player1x, player1y, 92, 92);


            ImageIcon imgPlayer2 = new ImageIcon(Objects.requireNonNull(getClass().getResource("Imagens/player_2_S.png")));
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
            this.setSize(700, 700);
            this.setVisible(true);
            this.setResizable(false);
        }

        public static void comunicarVitoria(char playerVencedor, boolean desistencia)
        {
            if (playerVencedor == playerControlante)
            {
                resultado.setForeground(Color.GREEN);
                if (desistencia)
                {
                    resultado.setText("Ganhou por desistência!");
                    resultado.setBounds(160, 275, 450, 75);
                    resultado.setFont(new Font("Monospace", Font.BOLD, 35));
                }
                else
                    resultado.setText("Ganhou!");
            }
            else
            {
                resultado.setText("Perdeu!");
                resultado.setForeground(Color.RED);
            }

            desabilitarEventos();
            resultado.setVisible(true);
        }

        public static void AtualizarTela()
        {
            player1.setBounds(player1x, player1y, 92, 92);
            player2.setBounds(player2x, player2y, 92, 92);

            try
            {
                if (player1x <= 58 || player1x >= 550 || player1y >= 550 || player1y <= 58) {
                    servidor.receba(new ComunicadoDeVitoria('L', false)); isFinished = true; }

                if (player2x <= 58 || player2x >= 550 || player2y >= 550 || player2y <= 58) {
                    servidor.receba(new ComunicadoDeVitoria('A', false)); isFinished = true; }
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
                    if (!isFinished)
                    {
                        if (playerControlante == 'A')
                            servidor.receba(new ComunicadoDeVitoria('L', true));
                        else
                            servidor.receba(new ComunicadoDeVitoria('A', true));
                    }
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
                        if (direcaoPlayerControlante == 'S' || direcaoPlayerControlante == 'O' || direcaoPlayerControlante == 'L')
                            Cliente.mandarRotacao(playerControlante, 'N');
                        else
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