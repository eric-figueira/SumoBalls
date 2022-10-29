import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;


public class Janela extends JFrame
{   
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
        ringue.setBorder(BorderFactory.createLineBorder(Color.BLACK, 7));

        Container cntForm = this.getContentPane();
        cntForm.add(titulo);
        cntForm.add(ringue);
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
