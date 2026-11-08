import java.awt.*;
import java.net.*;
import java.util.Scanner;

import static java.lang.Math.max;

public class Main {
    public static void main(String[] args) {
        Requester req = new Requester();
        Scanner scan = new Scanner(System.in);

        while (true) {
            System.out.print("Введите запрос: ");
            String search = scan.nextLine();
            if (search.equals("exit")) break;

            req.setRequest(search);
            int response_code = req.makeConnection();
            if (response_code == 200) {
                try {
                    Parser parser = new Parser(req.getConnectionResult());

                    var list = parser.getResults();

                    System.out.println("Было найдено %d совпадений.".formatted(list.size()));
                    if (list.isEmpty()) continue;
                    for (int i = 0; i < max(10, list.size()); i++) {
                        System.out.println("%d) %s".formatted(i + 1, list.get(i)));
                    }
                    System.out.print("Выберите нужный вам вариант: ");
                    int choose = Integer.parseInt(scan.nextLine());

                    if (choose < 0 || choose > max(10, list.size())) {
                        System.out.println("Введеного вами числа нет в допустимом диапозоне[0, %d]".formatted(max(10, list.size())));
                    }

                    String url = "https://ru.wikipedia.org/w/index.php?curid=" + parser.getPageId(choose);

                    Desktop desktop = Desktop.getDesktop();
                    URI uri = new URI(url);

                    desktop.browse(uri);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("Cry baby. Код ответа сервера: " + response_code);
            }
        }
        req.disconnect();
    }
}