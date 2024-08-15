import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

@SuppressWarnings("serial")
public class Settings extends JDialog implements ActionListener {
    MainPanel soru;
    JButton td, tc, tb, ap, di, cz, yt, ii, kd, al, da, sorubaslik, cevapbaslik, hints;
    JFontChooser fontChooser;
    final Settings ayarlar = this;

    public Settings(MainPanel soru) {
        super(soru, "Ayarlar", false);
        fontChooser = new JFontChooser(soru);
        this.soru = soru;
        int w = 170, h = 300;
        setBounds(soru.getX() + (soru.getWidth() - w) / 2, soru.getY() + (soru.getHeight() - h) / 2, w, h);
        setLayout(new GridLayout(0, 1));

        // Initialize buttons
        td = new JButton("Tablo Çerçeve Rengi");
        tc = new JButton("Tablo Çizgi Rengi");
        tb = new JButton("Tablo İçi Rengi");
        ap = new JButton("Arkaplan Rengi");
        ii = new JButton("İç İpucu Rengi");
        di = new JButton("Dış İpucu Rengi");
        da = new JButton("Dış İpucu Arkaplan Rengi");
        yt = new JButton("Yazı Tipi");
        cz = new JButton("Çiçek Rengi");
        kd = new JButton("Kaydet");
        al = new JButton("Al");
        hints = new JButton("Aritmetik Sonuç Rengi");
        sorubaslik = new JButton("Soru Başlığı");
        cevapbaslik = new JButton("Cevap Başlığı");

        // Add action listeners
        td.addActionListener(this);
        da.addActionListener(this);
        tc.addActionListener(this);
        tb.addActionListener(this);
        ap.addActionListener(this);
        di.addActionListener(this);
        yt.addActionListener(this);
        cz.addActionListener(this);
        ii.addActionListener(this);
        kd.addActionListener(this);
        al.addActionListener(this);
        hints.addActionListener(this);
        sorubaslik.addActionListener(this);
        cevapbaslik.addActionListener(this);

        // Add buttons to dialog
        add(tc); add(tb); add(ap);
        add(yt); add(kd); add(al);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == al) {
            new Al(soru);
        } else if (event.getSource() == kd) {
            String dosya = JOptionPane.showInputDialog(ayarlar, "Tablo düzenine isim veriniz", "Kaydet", JOptionPane.QUESTION_MESSAGE);
            if (dosya == null) return;
            try (BufferedWriter bw = new BufferedWriter(new FileWriter("TableOperations/tableOperations/ayarlar/" + dosya))) {
                bw.write((soru.questionPannel.arkaPlan != null ? soru.questionPannel.arkaPlan.getRGB() : Color.WHITE.getRGB()) + "");
                bw.newLine();
                bw.write((soru.questionPannel.cerceve != null ? soru.questionPannel.cerceve.getRGB() : Color.BLACK.getRGB()) + "");
                bw.newLine();
                bw.write((soru.questionPannel.tabloIc != null ? soru.questionPannel.tabloIc.getRGB() : Color.GRAY.getRGB()) + "");
                bw.newLine();
                bw.write((soru.questionPannel.tablo != null ? soru.questionPannel.tablo.getRGB() : Color.LIGHT_GRAY.getRGB()) + "");
                bw.newLine();
                bw.write((soru.questionPannel.cevap != null ? soru.questionPannel.cevap.getRGB() : Color.YELLOW.getRGB()) + "");
                bw.newLine();
                bw.write((soru.questionPannel.disipucu != null ? soru.questionPannel.disipucu.getRGB() : Color.RED.getRGB()) + "");
                bw.newLine();
                bw.write((soru.questionPannel.disarka != null ? soru.questionPannel.disarka.getRGB() : Color.BLUE.getRGB()) + "");
                bw.newLine();
                bw.write((soru.questionPannel.icipucu != null ? soru.questionPannel.icipucu.getRGB() : Color.GREEN.getRGB()) + "");
                bw.newLine();
                bw.write((soru.questionPannel.font != null ? soru.questionPannel.font.getFamily() : "Default Font"));
                bw.newLine();
                bw.write((soru.questionPannel.font != null ? soru.questionPannel.font.getStyle() : Font.PLAIN) + "");
                bw.newLine();
                bw.write((soru.questionPannel.font != null ? soru.questionPannel.font.getSize() : 12) + "");
                bw.newLine();
                bw.write((soru.questionPannel.sorubasligi != null ? soru.questionPannel.sorubasligi : "Default Soru Başlığı"));
                bw.newLine();
                bw.write((soru.questionPannel.cevapbasligi != null ? soru.questionPannel.cevapbasligi : "Default Cevap Başlığı"));
                bw.newLine();
                bw.write(soru.getX() + ""); bw.newLine();
                bw.write(soru.getY() + ""); bw.newLine();
                bw.write(soru.getWidth() + ""); bw.newLine();
                bw.write(soru.getHeight() + ""); bw.newLine();
                bw.write(soru.questionPannel.question != null ? soru.questionPannel.question.size + "" : "0"); bw.newLine();
                bw.write(soru.questionPannel.answer != null ? soru.questionPannel.answer.size + "" : "0"); bw.newLine();
                soru.questionPannel.kayitli = true;
                soru.questionPannel.duzen = dosya;
            } catch (IOException e) {
                JOptionPane.showMessageDialog(ayarlar, "Dosya kaydedilemedi!", "Hata", JOptionPane.ERROR_MESSAGE);
            }
        } else if (event.getSource() == td) {
            Color selectedColor = JColorChooser.showDialog(ayarlar, "Tablo Çerçeve Rengi Seç", soru.questionPannel.cerceve);
            if (selectedColor != null) {
                soru.questionPannel.cerceve = selectedColor;
            }
        } else if (event.getSource() == tc) {
            Color selectedColor = JColorChooser.showDialog(ayarlar, "Tablo Çizgi Rengi Seç", soru.questionPannel.tablo);
            if (selectedColor != null)  {
                soru.questionPannel.tablo = selectedColor;
                soru.questionPannel.kayitli = false;
            }
        } else if (event.getSource() == tb) {
            Color selectedColor = JColorChooser.showDialog(ayarlar, "Tablo İçi Rengi Seç", soru.questionPannel.tabloIc);
            if (selectedColor != null) {
                soru.questionPannel.tabloIc = selectedColor;
                soru.questionPannel.kayitli = false;
            }


        } else if(event.getSource()==ap)
        {   Color c = JColorChooser.showDialog(null,"Arkaplan Rengini Se\u00e7iniz",soru.questionPannel.getBackground());
            if(c!=null)
            {
                soru.questionPannel.arkaPlan = c;
                soru.questionPannel.setBackground(soru.questionPannel.arkaPlan);
                soru.getContentPane().setBackground(soru.questionPannel.arkaPlan);
                soru.controlPanel.description.setBackground(c);
                soru.questionPannel.kayitli = false;
            }
        } else if (event.getSource() == ii) {
            Color selectedColor = JColorChooser.showDialog(ayarlar, "İç İpucu Rengi Seç", soru.questionPannel.icipucu);
            if (selectedColor != null) soru.questionPannel.icipucu = selectedColor;
        } else if (event.getSource() == di) {
            Color selectedColor = JColorChooser.showDialog(ayarlar, "Dış İpucu Rengi Seç", soru.questionPannel.disipucu);
            if (selectedColor != null) soru.questionPannel.disipucu = selectedColor;
        } else if (event.getSource() == da) {
            Color selectedColor = JColorChooser.showDialog(ayarlar, "Dış İpucu Arkaplan Rengi Seç", soru.questionPannel.disarka);
            if (selectedColor != null) soru.questionPannel.disarka = selectedColor;
        } else if (event.getSource() == hints) {
            Color selectedColor = JColorChooser.showDialog(ayarlar, "Aritmetik Sonuç Rengi Seç", soru.questionPannel.hints);
            if (selectedColor != null) soru.questionPannel.hints = selectedColor;
        } else if (event.getSource() == cz) {
            Color selectedColor = JColorChooser.showDialog(ayarlar, "Çiçek Rengi Seç", soru.questionPannel.c1);
            if (selectedColor != null) soru.questionPannel.c1 = selectedColor;
        } else if (event.getSource() == yt) {
            int result = fontChooser.showDialog(soru.questionPannel.font);

            if (result != JFontChooser.CANCEL_OPTION) {
                soru.questionPannel.font = fontChooser.getFont();
                soru.questionPannel.setFont(soru.questionPannel.font);
                soru.controlPanel.description.setFont(soru.questionPannel.font);
                soru.questionPannel.kayitli = false;
            }
        } else if (event.getSource() == sorubaslik) {
            String newText = JOptionPane.showInputDialog(ayarlar, "Yeni Soru Başlığı", soru.questionPannel.sorubasligi);
            if (newText != null) soru.questionPannel.sorubasligi = newText;
        } else if (event.getSource() == cevapbaslik) {
            String newText = JOptionPane.showInputDialog(ayarlar, "Yeni Cevap Başlığı", soru.questionPannel.cevapbasligi);
            if (newText != null) soru.questionPannel.cevapbasligi = newText;
        }
        soru.repaint();
    }
}
