import java.awt.*;
import java.io.IOException;
import java.net.*;
import java.util.List;
import java.util.Scanner;

import static java.lang.Math.min;

public class Main {
    private static final int MAX_COUNT_OF_RESULTS = 10;

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

                    java.util.List<String> list = parser.getResults();

                    System.out.println("Было найдено %d совпадений.".formatted(list.size()));
                    if (list.isEmpty()) continue;
                    boolean moreVar = showTotalHits(list, scan);
                    int choice = getChoice(scan, (moreVar? list.size():min(MAX_COUNT_OF_RESULTS, list.size())));
                    if (choice == -1) break;

                    openPage(parser.getPageId(choice));
                } catch (IOException e) {
                    System.out.println("Не удалось получить данные: " + e.getMessage());
                }
            } else {
                errorResponse(response_code);
            }
            req.disconnect();
        }
    }
    public static boolean showTotalHits(List<String> list, Scanner scan) {
        for (int i = 0; i < min(MAX_COUNT_OF_RESULTS, list.size()); i++) {
            System.out.println("%d) %s".formatted(i + 1, list.get(i)));
            if (i + 1 == list.size()) return false;
        }

        char choice = '\0';
        while (choice != 'Y' && choice != 'n') {
            System.out.println("Показать больше вариантов?[Y/n]: ");
            choice = scan.next().charAt(0);
        }

        if (choice == 'n') return false;

        for (int i = MAX_COUNT_OF_RESULTS; i < list.size(); i++) {
            System.out.println("%d) %s".formatted(i + 1, list.get(i)));
        }

        return true;
    }

    public static void errorResponse(int response_code) {
        switch (response_code) {
            case -1 -> System.out.println("Не удалось подключиться к серверу. Проверьте подключение к интернету.");
            case 414 -> System.out.println("Запрос слишком длинный для сервера.");
            case 429 -> System.out.println("Слишком много запросов. Подождите немного и попробуйте снова.");
            default -> System.out.println("Код ответа сервера: " + response_code);
        }
    }

    public static int getChoice(Scanner scan, int max) {
        int choice = 0;
        String input;

        while (true) {
            System.out.print("Выберите нужный вам вариант: ");
            input = scan.nextLine();

            if (input.equals("exit")) {
                return -1;
            } else {
                try {
                    choice = Integer.parseInt(input);
                } catch (NumberFormatException e) {
                    System.out.println("Необходимо ввести цифры, а не любые иные символы.");
                    continue;
                }
            }

            if (choice < 0 || choice > max) {
                System.out.println("Введеного вами числа нет в допустимом диапозоне[0, %d]".formatted(max));
                continue;
            }

            break;
        }

        return choice;
    }

    public static void openPage(String pageid) {
        String url = "https://ru.wikipedia.org/w/index.php?curid=" + pageid;

        try {
            Desktop desktop = Desktop.getDesktop();
            if (desktop.isSupported(Desktop.Action.BROWSE)) {
                URI uri = new URI(url);

                desktop.browse(uri);
            } else {
                System.out.println("Не удалось открыть браузер на данной платформе.");
            }
        } catch (Exception e) {
            System.out.println("При открытие следующей ссылки произошла ошибка: " + url);
        }
    }
}
