import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {

        CoreTesting case1 = new CoreTesting();
        case1.FileToArray();		    //Подготавливает строчки из файла для тестирования
        case1.PhrasalTesting();			//Начинает тестирование, запрашивает тип тестирования
        case1.TestingSummery();			//Выдает на экран резцльтаты тестирования
    }

}