import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


@SuppressWarnings("serial")
public class Al extends JDialog {
    final JList list = new JList();
    QuestionPannel panel;
    final Al al = this;
    Al(MainPanel cicekler) {
        super(cicekler,"Renk Düzeni Seciniz",true);
        panel = cicekler.questionPannel;
        int w = 380, h = 240;
        setBounds(cicekler.getX()+(cicekler.getWidth()-w)/2,
                cicekler.getY()+(cicekler.getHeight()-h)/2,w,h);
        setBackground(panel.arkaPlan);
        getContentPane().setBackground(panel.arkaPlan);
        setLayout(null);
        listeAl();
        list.addListSelectionListener(new ListSelectionListener(){
            public void valueChanged(ListSelectionEvent e) {
                String name = (String)(list.getSelectedValue());
                if(name==null) return;
                panel.duzen = name;
                panel.kayitli = true;
                if(name.charAt(0)=='*')
                    name = name.substring(1);
                panel.duzenAl(name);
                al.setBackground(al.panel.arkaPlan);
                al.getContentPane().setBackground(al.panel.arkaPlan);
            }});
        list.setFont(new Font("Tahoma",Font.PLAIN,11));
        JScrollPane scroll = new JScrollPane(list);
        scroll.setBounds(10,10,230,180);
        add(scroll);
        if(panel.kayitli) {
            list.setSelectedValue("*"+panel.duzen,true);
            list.setSelectedValue(panel.duzen,true);
        }
        JButton b = new JButton("Varsayılan yap");
        b.setFont(new Font("Tahoma",Font.PLAIN,11));
        b.setBounds(250,10,111,25);
        b.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                int selected = list.getSelectedIndex();
                String name = (String)(list.getSelectedValue());
                if(name != null) {
                    if(name.charAt(0)=='*')
                        name = name.substring(1);
                    try {
                        BufferedWriter bw = new BufferedWriter(
                                new FileWriter("tableOperations2/tableOperations/vs.dat")
                        );
                        bw.write(name);
                        bw.close();
                        listeAl();
                        list.setSelectedIndex(selected);
                    } catch(IOException ex) {
                        JOptionPane.showMessageDialog(al,"Varsayılan yapılamadı","Hata",JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        add(b);

        b = new JButton("D\u00fczeni sil");
        b.setFont(new Font("Tahoma",Font.PLAIN,11));
        b.setBounds(250,40,111,25);
        b.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                int selected = list.getSelectedIndex();
                String name = (String)(list.getSelectedValue());
                boolean varsayilan = false;
                if(name==null) return;
                if(list.getModel().getSize()<2) {
                    JOptionPane.showMessageDialog(al,"Tek renk d\u00fczenini silemezsiniz","Hata",JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if(name.charAt(0)=='*') {
                    name = name.substring(1);
                    varsayilan = true;
                }
                if(new File("ayarlar/"+name).delete())
                {
                    listeAl();
                    list.setSelectedIndex(selected-1);
                    list.setSelectedIndex(selected);
                    if(varsayilan)
                    {
                        String yeni = "";
                        try {
                            yeni = (String)(list.getModel().getElementAt(selected));
                        } catch(ArrayIndexOutOfBoundsException ex) {
                            yeni = (String)(list.getModel().getElementAt(selected-1));
                        }
                        if(yeni==null)
                            return;
                        try {
                            BufferedWriter bw = new BufferedWriter(
                                    new FileWriter("tableOperations2/tableOperations/vs.dat")
                            );
                            bw.write(yeni);
                            bw.close();
                            listeAl();
                        } catch(IOException ex) {
                        }
                        list.setSelectedValue("*"+yeni,true);
                    }
                }
                else
                    JOptionPane.showMessageDialog(al,"Renk d\u00fczeni silinemedi","Hata",JOptionPane.ERROR_MESSAGE);
            }});
        add(b);

        b = new JButton("\u0130sim de\u011fi\u015ftir");
        b.setFont(new Font("Tahoma",Font.PLAIN,11));
        b.setBounds(250,70,111,25);
        b.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent arg0) {
                boolean varsayilan = false;
                String name = (String)(list.getSelectedValue());
                if(name==null) return;
                if(name.charAt(0)=='*') {
                    name = name.substring(1);
                    varsayilan = true;
                }
                String rename = JOptionPane.showInputDialog(al,"Yeni ismi giriniz","\u0130sim de\u011fi\u015ftir",JOptionPane.QUESTION_MESSAGE);
                if(rename==null) return;
                if(!new File("ayarlar/"+name).renameTo(new File("ayarlar/"+rename)))
                    JOptionPane.showMessageDialog(al,"\u0130sim de\u011fi\u015ftirilemedi","Hata",JOptionPane.ERROR_MESSAGE);
                else
                {
                    if(varsayilan) {
                        try {
                            BufferedWriter bw = new BufferedWriter(
                                    new FileWriter("tableOperations2/tableOperations/vs.dat")
                            );
                            bw.write(rename);
                            bw.close();
                            listeAl();
                        } catch(IOException ex) {
                        }
                        rename = "*"+rename;
                    }
                    panel.duzen = rename;
                    listeAl();
                    list.setSelectedValue(rename,true);
                }
            }});
        add(b);
        setVisible(true);
    }
    private void listeAl() {
        String varsayilan = "*";
        try {
            BufferedReader br = new BufferedReader(new FileReader("TableOperations/tableOperations/vs.dat"));
            varsayilan = br.readLine();
            br.close();
        } catch(IOException e) {}
        File dir = new File("TableOperations/tableOperations/ayarlar");
        File[] files = dir.listFiles();
        String[] filenames = new String[files.length];
        for(int i = 0; i < files.length; i++) {
            filenames[i] = files[i].getName();
            if(filenames[i].equals(varsayilan))
                filenames[i] = "*"+filenames[i];
        }
        list.setListData(filenames);
    }
}
