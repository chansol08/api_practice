package practice.geocode;

import javax.swing.*;
import java.awt.*;

public class MapGUI {

    JTextField address;
    JLabel resAddress;
    JLabel jibunAddress;
    JLabel resX;
    JLabel resY;
    JLabel imageLabel;

    public void initGUI() {

        JFrame form = new JFrame("Map view");
        form.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        form.setLocationRelativeTo(null);

        Container con = form.getContentPane();

        JPanel header = new JPanel();
        JLabel inputLabel = new JLabel("주소입력");
        imageLabel = new JLabel("지도보기");
        address = new JTextField(50);

        JButton btn = new JButton("클릭");
        btn.addActionListener(new GeocodeMapService(this));

        header.add(inputLabel);
        header.add(address);
        header.add(btn);

        JPanel pan1 = new JPanel();
        pan1.setLayout(new GridLayout(4, 1));
        resAddress = new JLabel("도로명");
        jibunAddress = new JLabel("지번주소");
        resX = new JLabel("경도");
        resY = new JLabel("위도");

        pan1.add(resAddress);
        pan1.add(jibunAddress);
        pan1.add(resX);
        pan1.add(resY);

        con.add(BorderLayout.NORTH, header);
        con.add(BorderLayout.CENTER, imageLabel);
        con.add(BorderLayout.SOUTH, pan1);

        form.setSize(730, 660);
        form.setVisible(true);
    }

    public static void main(String[] args) {
        new MapGUI().initGUI();
    }
}
