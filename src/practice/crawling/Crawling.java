package practice.crawling;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Jsoup API practice
 */
public class Crawling {

    public static void main(String[] args) {
        //sum.su.or.kr:8888/bible/today => crawling 주소
        String url = "https://sum.su.or.kr:8888/bible/today/Ajax/Bible/BosyMatter?qt_ty=QT1"; //요청 url
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in)); //날짜 입력

        try {
            System.out.print("[입력 -> 년(yyyy)-월(mm)-일(dd)]: ");
            String bible = br.readLine();
            url = url + "&Base_de=" + bible + "&bibleType=1"; //요청 url
            System.out.println("================================");
            Document doc = Jsoup.connect(url).post(); //post 방식 요청

            //요소를 select 변수에 저장 - 출력
            Element bible_text = doc.select(".bible_text").first();
            System.out.println(bible_text.text());

            Element bibleInfo_box = doc.select(".bibleinfo_box").first();
            System.out.println(bibleInfo_box.text());

            //여러 요소 선택 for 문으로 출력
            Elements liList = doc.select(".body_list > li");

            for (Element li : liList) {
                System.out.print(li.select(".num").first().text() + " : ");
                System.out.println(li.select(".info").first().text());
            }

            //리소스 다운 mp3
            /*
            Element tag = doc.select("source").first();
            String dPath = tag.attr("src").trim();
            System.out.println(dPath); //https://meditation.su.or.kr/meditation_mp3/2019/20191010.mp3
            String fileName = dPath.substring(dPath.lastIndexOf("/") + 1);
             */

            //리소스 다운 image
            Element tag = doc.select(".img > img").first();
            String dPath = "https://sum.su.or.kr:8888" + tag.attr("src").trim();
            System.out.println(dPath); //https://sum.su.or.kr:8888/attach/X07/597dbbf2797f469b9f1fa3d0eec2d4c3.jpg
            String fileName = dPath.substring(dPath.lastIndexOf("/") + 1);
            Runnable r = new DownloadBroker(dPath, fileName);
            Thread dLoad = new Thread(r);
            dLoad.start();

            for (int i = 0; i < 10; i++) {
                try {
                    Thread.sleep(1000);
                } catch(Exception e) {
                    e.printStackTrace();
                }
                System.out.print("" + (i + 1));
            }

            System.out.println();
            System.out.println("================================");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
