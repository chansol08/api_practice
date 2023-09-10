package practice.crawling;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class CrawlingGUI extends JFrame implements ActionListener, ItemListener {
    private Choice chyear;
    private Choice chmonth;
    private JLabel yLabel;
    private JLabel mLabel;
    private JTextArea area;
    GregorianCalendar gc;
    private int year;
    private int month;
    private JLabel[] dayLabel = new JLabel[7];
    private String[] day = {"일", "월", "화", "수", "목", "금", "토"};
    private JButton[] days = new JButton[42]; //7일이 6주 이므로 42개의 버튼 필요
    private JPanel selectPanel = new JPanel();
    private GridLayout grid = new GridLayout(7,7,5,5); //행, 열, 수평 갭, 수직 갭
    private Calendar ca = Calendar.getInstance();
    private Dimension dimen1;
    private Dimension dimen2;
    private int xpos;
    private int ypos;

    public CrawlingGUI() {
        setTitle("오늘의 QT : " + ca.get(Calendar.YEAR) + "/" + (ca.get(Calendar.MONTH) + 1) + "/" + ca.get(Calendar.DATE));
        setSize(900,600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        dimen1 = Toolkit.getDefaultToolkit().getScreenSize();
        dimen2 = this.getSize();
        xpos = (int) (dimen1.getWidth() / 2 - dimen2.getWidth() / 2);
        ypos = (int) (dimen1.getHeight() / 2 - dimen2.getHeight() / 2);
        setLocation(xpos, ypos);
        setResizable(false);
        setVisible(true);
        chyear = new Choice();
        chmonth = new Choice();
        yLabel = new JLabel("년");
        mLabel = new JLabel("월");
        init();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        area.setText("");
        String year = chyear.getSelectedItem();
        String month = chmonth.getSelectedItem();
        JButton btn = (JButton) e.getSource();
        String day = btn.getText();
        System.out.println(year + ", " + month + ", " + day);
        String bible = year + "-" + month + "-" + day;
        //Jsoup API : HTML 파싱 방법
        //String url = "https://sum.su.or.kr:8888/bible/today";
        String url = "https://sum.su.or.kr:8888/bible/today/Ajax/Bible/BodyMatter?qt_ty=QT1&Base_de=" + bible + "&bibleType=1";

        try {
            Document doc = Jsoup.connect(url).post();

            //bible_text
            Element bible_text = doc.select(".bible_text").first();
            System.out.println(bible_text.text());

            //bibleInfo_box
            Element bibleInfo_box = doc.select("#bibleinfo_box").first();
            System.out.println(bibleInfo_box.text());

            //dailyBible_info
            Element dailyBible_info = doc.select("#dailybible_info").first();
            System.out.println(dailyBible_info.text());

            area.append(dailyBible_info.text() + "\n");
            area.append(bibleInfo_box.text() + "\n");
            area.append(bible_text.text() + "\n");

            Elements liList = doc.select(".body_list > li");

            for (Element li : liList) {
                String line = li.select(".info").first().text();

                //줄이 길다면 개행 해주기
                if (line.length() > 65) {
                    line = line.substring(0, 36) + "\n" + line.substring(36, 66) + "\n" + line.substring(66) + "\n";
                    area.append(li.select(".num").first().text() + " : " + line);
                } else if (line.length() > 35) {
                    line = line.substring(0, 36) + "\n" + line.substring(36) + "\n";
                    area.append(li.select(".num").first().text() + " : " + line);
                } else {
                    area.append(li.select(".num").first().text() + " : " + li.select(".info").first().text() + "\n");
                }

                System.out.print(li.select(".num").first().text() + " : ");
                System.out.println(li.select(".info").first().text());
            }
        } catch (Exception error) {
            error.printStackTrace();
        }
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        Color color = this.getBackground();

        if (e.getStateChange() == ItemEvent.SELECTED) {
            for (int i = 0; i < 42; i++) { //년이나 월이 선택 되면 기존의 달력을 지우고 새로 그린다.
                if (!days[i].getText().equals("")) {
                    days[i].setText(""); //기존의 날짜를 지움
                    days[i].setBackground(color); //달력의 배경색과 동일한 색으로 버튼의 배경색을 설정함.
                }
            }

            calendar();
        }
    }

    public void select() {
        JPanel panel = new JPanel(grid); //7행 7열의 그리드

        for (int i = 2023; i >= 2000; i--) {
            chyear.add(String.valueOf(i));
        }

        for (int i = 1; i <= 12; i++) {
            chmonth.add(String.valueOf(i));
        }

        //요일 출력
        for (int i = 0; i < day.length; i++) {
            dayLabel[i] = new JLabel(day[i], JLabel.CENTER);
            panel.add(dayLabel[i]);
            dayLabel[i].setBackground(Color.GRAY);
        }

        dayLabel[6].setForeground(Color.BLUE); //토요일 색상
        dayLabel[0].setForeground(Color.RED); //일요일 색상

        for (int i = 0; i < 42; i++) {
            days[i] = new JButton("");

            if (i % 7 == 0) {
                days[i].setForeground(Color.RED); //일요일 색상
            } else if (i % 7 == 6) {
                days[i].setForeground(Color.BLUE); //토요일 색상
            } else {
                days[i].setForeground(Color.BLACK);
            }

            days[i].addActionListener(this);
            panel.add(days[i]);
        }

        selectPanel.add(chyear);
        selectPanel.add(yLabel);
        selectPanel.add(chmonth);
        selectPanel.add(mLabel);

        area = new JTextArea(60, 40);
        area.setCaretPosition(area.getDocument().getLength());
        JScrollPane scrollPane = new JScrollPane(area);
        this.add(selectPanel, "North");
        this.add(panel, "Center");
        this.add(scrollPane, "East");

        String m = (ca.get(Calendar.MONTH) + 1) + "";
        String y = ca.get(Calendar.YEAR) + "";
        chyear.select(y);
        chmonth.select(m);
        chyear.addItemListener(this);
        chmonth.addItemListener(this);
    }

    public void calendar() {
        year = Integer.parseInt(chyear.getSelectedItem());
        month = Integer.parseInt(chmonth.getSelectedItem());
        gc = new GregorianCalendar(year, month - 1, 1);
        int max = gc.getActualMaximum(gc.DAY_OF_MONTH); //해당 달의 최대 일 수 획득
        int week = gc.get(gc.DAY_OF_WEEK); //해당 달의 시작 요일
        String today = Integer.toString(ca.get(Calendar.DATE)); //오늘 날짜 획득
        String today_month = Integer.toString(ca.get(Calendar.MONTH) + 1); //오늘의 달 획득

        for (int i = 0; i < days.length; i++) {
            days[i].setEnabled(true);
        }

        for (int i = 0; i < week - 1; i++) {
            days[i].setEnabled(false); //시작 일의 이전 버튼을 비활성화
        }

        for (int i = week; i < max + week; i++) {
            days[i - 1].setText((String.valueOf(i - week + 1)));
            days[i - 1].setBackground(Color.WHITE);

            if (today_month.equals(String.valueOf(month))) { //오늘이 속한 달과 같은 달인 경우
                if (today.equals(days[i - 1].getText())) { //버튼의 날짜와 오늘 날짜가 일치하는 경우
                    days[i - 1].setBackground(Color.CYAN); //버튼의 배경색 지정
                }
            }
        }

        for (int i = (max + week - 1); i < days.length; i++) { //날짜가 없는 버튼을 비활성화
            days[i].setEnabled(false);
        }
    }

    public void init() {
        select();
        calendar();
    }

    public static void main(String[] args) {
        new CrawlingGUI();
    }
}
