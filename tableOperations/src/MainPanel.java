import  java.awt.*;
import javax.swing.*;

@SuppressWarnings("serial")
public class MainPanel extends JFrame {
    public QuestionPannel questionPannel;
    ControlPanel controlPanel;
    QuestionInputPanel questionInputPanel;
    public MainPanel() {
        setTitle("Tablo İşlemleri");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setBounds(20, 100, 1200, 800);
        questionPannel = new QuestionPannel(this);
        controlPanel = new ControlPanel(this);
        questionInputPanel = new QuestionInputPanel(this);
        createRootPane();
        getContentPane().add(questionInputPanel, BorderLayout.NORTH);
        getContentPane().add(questionPannel, BorderLayout.CENTER);
        getContentPane().add(controlPanel, BorderLayout.SOUTH);
        setLocationRelativeTo(null);
        setVisible(true);
    }


    public static void main(String[] args) {
        try {
            //Nimbus temasını arıyoruz
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            try {
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            } catch (Exception exp) {
                exp.printStackTrace();
            }
        }
        SwingUtilities.invokeLater(MainPanel::new);
    }
}

