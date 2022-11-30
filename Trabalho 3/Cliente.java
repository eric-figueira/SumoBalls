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
    private static JLabel playerAzul = null;
    private static JLabel playerLaranja = null;


    // Posicoes iniciais e direcao jogador 1
    private static final int IniPlayerAzulx = 200;
    private static final int IniPlayerAzuly = 200;
    private static int playerAzulx = IniPlayerAzulx;
    private static int playerAzuly = IniPlayerAzuly;


    // Posicoes iniciais e direcao jogador 2
    private static final int IniPlayerLaranjax = 425;
    private static final int IniPlayerLaranjay = 425;
    private static int playerLaranjax = IniPlayerLaranjax;
    private static int playerLaranjay = IniPlayerLaranjay;


    // Quando os jogadores vao se mexer
    private static int ESCALA_MOVIMENTACAO = 20;

    private static char playerControlante;
    private static char direcaoPlayerControlante;

    private static boolean isMatchFinished = false;

    static Janela janela = null;

    private static Cliente.Janela.keyHandler kh;


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
        TratadoraDeComunicadoDeVitoria tratadoraDeComunicadoDeVitoria = null;
        TratadoraJogador tratadoraJogador = null;

        try {
            tratadoraDeComunicadoDeDesligamento = new TratadoraDeComunicadoDeDesligamento(servidor);
            tratadoraDeComunicadoDeVitoria = new TratadoraDeComunicadoDeVitoria(servidor);
            tratadoraJogador = new TratadoraJogador(servidor);
        }
        catch (Exception erro) { }

        tratadoraDeComunicadoDeDesligamento.start();
        tratadoraDeComunicadoDeVitoria.start();
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
        if (index == 0) {
            playerControlante = 'A';
            direcaoPlayerControlante = 'N';
            Janela.time.setText("Time: Azul");
        }
        else if (index == 1) {
            playerControlante = 'L'; direcaoPlayerControlante = 'S';
            servidor.receba(new ComunicadoDeInicio());
            Janela.time.setText("Time: Laranja");
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
            if (playerMovimentante == 'A') playerAzuly -= ESCALA_MOVIMENTACAO;
            if (playerMovimentante == 'L') playerLaranjay -= ESCALA_MOVIMENTACAO;
        }
        else if (direcaoMovimento == 'O')
        {
            if (playerMovimentante == 'A') playerAzulx -= ESCALA_MOVIMENTACAO;
            if (playerMovimentante == 'L') playerLaranjax -= ESCALA_MOVIMENTACAO;
        }
        else if (direcaoMovimento == 'S')
        {
            if (playerMovimentante == 'A') playerAzuly += ESCALA_MOVIMENTACAO;
            if (playerMovimentante == 'L') playerLaranjay += ESCALA_MOVIMENTACAO;
        }
        else if (direcaoMovimento == 'L')
        {
            if (playerMovimentante == 'A') playerAzulx += ESCALA_MOVIMENTACAO;
            if (playerMovimentante == 'L') playerLaranjax += ESCALA_MOVIMENTACAO;
        }

        Janela.AtualizarTela();
    }

    public static void realizarRotacao(char playerRotante, char direcaoRotacao)
    {
        if (direcaoRotacao == 'N')
        {
            if (playerRotante == 'A')
            {
                playerAzuly -= ESCALA_MOVIMENTACAO / 2;
                playerAzul.setIcon(new ImageIcon(Objects.requireNonNull(Janela.class.getResource("Imagens/player_1_N.png"))));
            }
            if (playerRotante == 'L')
            {
                playerLaranjay -= ESCALA_MOVIMENTACAO / 2;
                playerLaranja.setIcon(new ImageIcon(Objects.requireNonNull(Janela.class.getResource("Imagens/player_2_N.png"))));
            }
        }
        else if (direcaoRotacao == 'S')
        {
            if (playerRotante == 'A')
            {
                playerAzuly += ESCALA_MOVIMENTACAO / 2;
                playerAzul.setIcon(new ImageIcon(Objects.requireNonNull(Janela.class.getResource("Imagens/player_1_S.png"))));
            }
            if (playerRotante == 'L')
            {
                playerLaranjay += ESCALA_MOVIMENTACAO / 2;
                playerLaranja.setIcon(new ImageIcon(Objects.requireNonNull(Janela.class.getResource("Imagens/player_2_S.png"))));
            }
        }
        else if (direcaoRotacao == 'O')
        {
            if (playerRotante == 'A')
            {
                playerAzulx -= ESCALA_MOVIMENTACAO / 2;
                playerAzul.setIcon(new ImageIcon(Objects.requireNonNull(Janela.class.getResource("Imagens/player_1_O.png"))));
            }
            if (playerRotante == 'L')
            {
                playerLaranjax -= ESCALA_MOVIMENTACAO / 2;
                playerLaranja.setIcon(new ImageIcon(Objects.requireNonNull(Janela.class.getResource("Imagens/player_2_O.png"))));
            }
        }
        else if (direcaoRotacao == 'L')
        {
            if (playerRotante == 'A')
            {
                playerAzulx += ESCALA_MOVIMENTACAO / 2;
                playerAzul.setIcon(new ImageIcon(Objects.requireNonNull(Janela.class.getResource("Imagens/player_1_L.png"))));
            }
            if (playerRotante == 'L')
            {
                playerLaranjax += ESCALA_MOVIMENTACAO / 2;
                playerLaranja.setIcon(new ImageIcon(Objects.requireNonNull(Janela.class.getResource("Imagens/player_2_L.png"))));
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
            if ((playerAzulx + tamanho/4) >= playerLaranjax && (playerAzulx + 3*tamanho/4) >= playerLaranjax)
            {
                if (Math.abs(playerLaranjay - playerAzuly) <= tamanho * 1.25)
                    realizarMovimentacao(playerAtacado, 'N');
            }
        }
        else if (direcaoAtaque == 'S')
        {
            if ((playerAzulx + tamanho/4) >= playerLaranjax && (playerAzulx + 3*tamanho/4) >= playerLaranjax)
            {
                if (Math.abs(playerAzuly - playerLaranjay) <= tamanho * 1.25)
                    realizarMovimentacao(playerAtacado, 'S');
            }
        }
        else if (direcaoAtaque == 'L')
        {
            if ((playerAzuly + tamanho/4) >= playerLaranjay && (playerAzuly + 3*tamanho/4) >= playerLaranjay)
            {
                if (Math.abs(playerAzulx - playerLaranjax) <= tamanho * 1.25)
                    realizarMovimentacao(playerAtacado, 'L');
            }
        }
        else if (direcaoAtaque == 'O')
        {
            if ((playerAzuly + tamanho/4) >= playerLaranjay && (playerAzuly + 3*tamanho/4) >= playerLaranjay)
            {
                if (Math.abs(playerLaranjax - playerAzulx) <= tamanho * 1.25)
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
                playerAzul.setIcon(new ImageIcon(Objects.requireNonNull(Janela.class.getResource("Imagens/player_1_N_ataque.png"))));
                Thread.sleep(delay);
                playerAzul.setIcon(new ImageIcon(Objects.requireNonNull(Janela.class.getResource("Imagens/player_1_N.png"))));
            }
            if (direcaoAtaque == 'S')
            {
                playerAzul.setIcon(new ImageIcon(Objects.requireNonNull(Janela.class.getResource("Imagens/player_1_S_ataque.png"))));
                Thread.sleep(delay);
                playerAzul.setIcon(new ImageIcon(Objects.requireNonNull(Janela.class.getResource("Imagens/player_1_S.png"))));
            }
            if (direcaoAtaque == 'L')
            {
                playerAzul.setIcon(new ImageIcon(Objects.requireNonNull(Janela.class.getResource("Imagens/player_1_L_ataque.png"))));
                Thread.sleep(delay);
                playerAzul.setIcon(new ImageIcon(Objects.requireNonNull(Janela.class.getResource("Imagens/player_1_L.png"))));
            }
            if (direcaoAtaque == 'O')
            {
                playerAzul.setIcon(new ImageIcon(Objects.requireNonNull(Janela.class.getResource("Imagens/player_1_O_ataque.png"))));
                Thread.sleep(delay);
                playerAzul.setIcon(new ImageIcon(Objects.requireNonNull(Janela.class.getResource("Imagens/player_1_O.png"))));
            }
        }
        else if (playerAtacante == 'L')
        {
            if (direcaoAtaque == 'N')
            {
                playerLaranja.setIcon(new ImageIcon(Objects.requireNonNull(Janela.class.getResource("Imagens/player_2_N_ataque.png"))));
                Thread.sleep(delay);
                playerLaranja.setIcon(new ImageIcon(Objects.requireNonNull(Janela.class.getResource("Imagens/player_2_N.png"))));
            }
            if (direcaoAtaque == 'S')
            {
                playerLaranja.setIcon(new ImageIcon(Objects.requireNonNull(Janela.class.getResource("Imagens/player_2_S_ataque.png"))));
                Thread.sleep(delay);
                playerLaranja.setIcon(new ImageIcon(Objects.requireNonNull(Janela.class.getResource("Imagens/player_2_S.png"))));
            }
            if (direcaoAtaque == 'L')
            {
                playerLaranja.setIcon(new ImageIcon(Objects.requireNonNull(Janela.class.getResource("Imagens/player_2_L_ataque.png"))));
                Thread.sleep(delay);
                playerLaranja.setIcon(new ImageIcon(Objects.requireNonNull(Janela.class.getResource("Imagens/player_2_L.png"))));
            }
            if (direcaoAtaque == 'O')
            {
                playerLaranja.setIcon(new ImageIcon(Objects.requireNonNull(Janela.class.getResource("Imagens/player_2_O_ataque.png"))));
                Thread.sleep(delay);
                playerLaranja.setIcon(new ImageIcon(Objects.requireNonNull(Janela.class.getResource("Imagens/player_2_O.png"))));
            }
        }
    }

    public static void iniciar() throws Exception
    {
        int delay = 1000;
        ESCALA_MOVIMENTACAO = 20;
        try
        {
            if (isMatchFinished) // Para nao fazermos uma reatribuicao com os mesmo valores
            {
                playerAzul.setBounds(IniPlayerAzulx, IniPlayerAzuly, tamanho, tamanho);
                playerLaranja.setBounds(IniPlayerLaranjax, IniPlayerLaranjay, tamanho, tamanho);

                playerAzulx = IniPlayerAzulx;
                playerAzuly = IniPlayerAzuly;
                playerLaranjax = IniPlayerLaranjax;
                playerLaranjay = IniPlayerLaranjay;

                playerAzul.setIcon(new ImageIcon(Objects.requireNonNull(Janela.class.getResource("Imagens/player_1_S.png"))));
                playerLaranja.setIcon(new ImageIcon(Objects.requireNonNull(Janela.class.getResource("Imagens/player_2_N.png"))));

                if (playerControlante == 'A') direcaoPlayerControlante = 'S';
                if (playerControlante == 'S') direcaoPlayerControlante = 'N';
            }

            Janela.resultado.setBounds(175, 275, 400, 75);
            Janela.resultado.setFont(new Font("Monospace", Font.BOLD, 35));
            Janela.resultado.setForeground(Color.DARK_GRAY);

            Janela.resultado.setText("Partida iniciando em:");
            Thread.sleep(delay);

            Janela.resultado.setBounds(340, 275, 400, 75);
            Janela.resultado.setFont(new Font("Monospace", Font.BOLD, 60));

            Janela.resultado.setText("3");
            Thread.sleep(delay);
            Janela.resultado.setText("2");
            Thread.sleep(delay);
            Janela.resultado.setText("1");
            Thread.sleep(delay);

            Janela.resultado.setBounds(270, 285, 400, 75);
            Janela.resultado.setText("Lutem!");

            Thread.sleep(delay);
            Janela.resultado.setVisible(false);

            isMatchFinished = false; // Começo da partida
            habilitarEventos();
        }
        catch (Exception erro)
        { }
    }

    public static void habilitarEventos()
    {
        kh = new Janela.keyHandler();
        janela.ObterJanela().addKeyListener(kh);
    }

    public static void desabilitarEventos()
    {
        janela.ObterJanela().removeKeyListener(kh);
    }

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
            this.setSize(275, 200);
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
        static JLabel time = new JLabel("Time: ");

        static JLabel placarAzul = new JLabel("A: 0");
        static JLabel placarLaranja = new JLabel("L: 0");

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


            // imagens dos jogadores
            ImageIcon imgPlayer1 = new ImageIcon(Objects.requireNonNull(getClass().getResource("Imagens/player_1_N.png")));
            playerAzul = new JLabel(imgPlayer1);
            playerAzul.setBounds(IniPlayerAzulx, IniPlayerAzuly, tamanho, tamanho);

            ImageIcon imgPlayer2 = new ImageIcon(Objects.requireNonNull(getClass().getResource("Imagens/player_2_S.png")));
            playerLaranja = new JLabel(imgPlayer2);
            playerLaranja.setBounds(IniPlayerLaranjax, IniPlayerLaranjay, tamanho, tamanho);

            Font fonte = new Font("Monospace", Font.BOLD, 35);

            time.setBounds(15, 600, 400, 40);
            time.setFont(fonte);
            time.setForeground(Color.LIGHT_GRAY);
            time.setVisible(true);

            placarAzul.setBounds(400, 600, 100, 40);
            placarAzul.setFont(fonte);
            placarAzul.setForeground(Color.BLUE);

            placarLaranja.setBounds(500, 600, 100, 40);
            placarLaranja.setFont(fonte);
            placarLaranja.setForeground(Color.ORANGE);

            fundo = new JLayeredPane();
            fundo.setSize(700, 700);
            fundo.add(titulo, 2);
            fundo.add(resultado,0);
            fundo.add(time, -1);
            fundo.add(placarAzul, -1);
            fundo.add(placarLaranja, -1);
            fundo.add(ringue, 2);
            fundo.add(playerAzul, 1);
            fundo.add(playerLaranja, 1);

            Container cntForm = this.getContentPane();
            cntForm.add(fundo);
            cntForm.setBackground(Color.DARK_GRAY);

            this.addWindowListener(new FechamentoDeJanela());
            this.setSize(700, 700);
            this.setVisible(true);
            this.setResizable(false);
        }

        static byte vezesVencidasAzul = 0;
        static byte vezesVencidasLaranja = 0;

        public static void comunicarVitoria(char playerVencedor, boolean desistencia)
        {
            if (playerVencedor == 'A') {
                vezesVencidasAzul += 1;
                Janela.placarAzul.setText("A: "+vezesVencidasAzul);
            }
            else {
                vezesVencidasLaranja += 1;
                Janela.placarLaranja.setText("L: "+vezesVencidasLaranja);
            }

            if (playerVencedor == playerControlante)
            {
                resultado.setForeground(Color.GREEN);
//                if (desistencia)
//                {
//                    resultado.setText("Ganhou por desistência!");
//                    resultado.setBounds(160, 275, 450, 75);
//                    resultado.setFont(new Font("Monospace", Font.BOLD, 35));
//                }
//                else
                    resultado.setText("Ganhou!");
            }
            else
            {
                resultado.setForeground(Color.RED);
                resultado.setText("Perdeu!");
            }
            resultado.setVisible(true);



            desabilitarEventos();
            if (!desistencia)
                try {
                    Thread.sleep(1500);
                    resultado.setForeground(Color.DARK_GRAY);
                    resultado.setBounds(160, 275, 450, 75);
                    resultado.setFont(new Font("Monospace", Font.BOLD, 27));
                    resultado.setText("Uma nova partida se iniciará");
                    Thread.sleep(2000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }

        public static void AtualizarTela()
        {
            playerAzul.setBounds(playerAzulx, playerAzuly, tamanho, tamanho);
            playerLaranja.setBounds(playerLaranjax, playerLaranjay, tamanho, tamanho);

            try
            {
                if (playerControlante == 'A')
                {
                    if (playerAzulx <= 58 || playerAzulx >= 550 || playerAzuly >= 550 || playerAzuly <= 58) {
                        servidor.receba(new ComunicadoDeVitoria('L', false));
                    }
                }
                else
                {
                    if (playerLaranjax <= 58 || playerLaranjax >= 550 || playerLaranjay >= 550 || playerLaranjay <= 58) {
                        servidor.receba(new ComunicadoDeVitoria('A', false));
                    }
                }

                isMatchFinished = true;
            }
            catch (Exception erro) { MostrarMensagemDeErro(erro.getMessage()); }
        }

        public static void MostrarMensagemDeErro(String erroRecebido)
        {
            JOptionPane.showMessageDialog(fundo, erroRecebido, "Um erro aconteceu", JOptionPane.ERROR_MESSAGE);
        }


        static class FechamentoDeJanela extends WindowAdapter {
            public void windowClosing(WindowEvent e) {
                try
                {
                    if (!isMatchFinished)
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