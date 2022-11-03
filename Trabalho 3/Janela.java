import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
//import java.util.*;
//import javax.imageio.*;


public class Janela extends JFrame
{   
    protected JLayeredPane fundo;

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

        JLabel player1;
        ImageIcon imgPlayer1 = new ImageIcon(getClass().getResource("Imagens/player_1.png"));
        player1 = new JLabel(imgPlayer1);
        player1.setBounds(200, 200, 75, 75);

        JLabel player2;
        ImageIcon imgPlayer2 = new ImageIcon(getClass().getResource("Imagens/player_2.png"));
        player2 = new JLabel(imgPlayer2);
        player2.setBounds(425, 425, 75, 75);

        this.fundo = new JLayeredPane();
        this.fundo.setSize(600, 600);
        this.fundo.add(titulo, 2);
        this.fundo.add(ringue, 2);
        this.fundo.add(player1, 1);
        this.fundo.add(player2, 1);

        Container cntForm = this.getContentPane();
        cntForm.add(this.fundo);
        cntForm.setBackground(Color.DARK_GRAY);

        this.addWindowListener(new FechamentoDeJanela());
        this.setSize(700, 700);
        this.setVisible(true);
        this.setResizable(false);
    }

    protected class FechamentoDeJanela extends WindowAdapter {
        public void windowClosing(WindowEvent e) {
            System.exit(0);
        }
    }
}
