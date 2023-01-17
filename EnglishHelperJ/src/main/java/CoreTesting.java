import java.io.*;
import java.security.SecureRandom;
import java.util.Scanner;

public class CoreTesting {
    char[][] QuestionArr = new char[300][100];                            //массив для хранения строк, содержащих вопросы
    char[][] AnswerArr = new char[300][100];                              //массив для хранения правильных ответов
    int QuestionNumber = 0;                                //общее число вопросов/ответов
    int[] RandomNumberArrPointer;                         //Указатель на массив со случайными числами
    int ErrorsNumber = 0;                                //общее количество ошибок
    int[] WrongAnswer = new int[300];                                //массив для хранения номеров вопросов/ответов, где была ошибка
    int[] MissedQuestions = new int[300];                            //массив для номеров вопросов которые были пропущены
    int WrongIndex = 0;                                    //индекс массива вопросов/ответов c ошибками
    int MissedIndex = 0;                                //индекс массива пропущенных впросов

    protected int TestingType = 0;				//хранит число, соответствующее типу тестирования
                                                //0-тест закончен
                                                //1-тест на знание фразовых конструкций

    CoreTesting() {
        TestingType = StartTesting();        //Вызывается метод для определения типа теста
    }

    static int StartTesting(){
        int testingType;
        Scanner read = new Scanner(System.in);
        System.out.println("Для проведения теста по Phrasal verbs, Prepositional phrases, Word patterns \nВведите '1'\n");
        System.out.println("Для завершения теста введите finish");
        System.out.println("Для пропуска вопроса введите next");
        testingType = read.nextInt();
        return testingType;
    }

    public int FileToArray() throws IOException {


        int answerIndexChar;
        int characterIndex;
        String bufferString;

        File DataFile =new File("C:\\Users\\Odd\\IdeaProjects\\EnglishHelperJ\\PhrasalV.txt");
        FileReader fr = new FileReader(DataFile);
        BufferedReader reader = new BufferedReader(fr);

        bufferString = reader.readLine();
        System.out.println(bufferString);

        String QuestionBuff;
        String AnswerBuff;

        while (bufferString!=null){                                                                     //пока не закончатся строки в файле

            if(bufferString.length()==0)
                bufferString = reader.readLine();

            characterIndex = bufferString.length();
            QuestionArr[QuestionNumber] = bufferString.toCharArray();							        //кладем строку в массив посимвольно

            for (int x = 0; x < characterIndex; x++) {													//цикл для определения "зоны ответа"
                if ((QuestionArr[QuestionNumber][x] == '\t')) {											//табуляция указывает на то, что после нее искомый для данной строчки предлог
                    QuestionArr[QuestionNumber][x] = ' ';												//убераем табуляцию
                    x++;
                    answerIndexChar = 0;																//обнуляем счетчик начального символа "ответа"
                    AnswerBuff = "";
                    QuestionBuff = "";

                    for(int y = 0 ; y < x ; y++){
                        QuestionBuff+=Character.toString(QuestionArr[QuestionNumber][y]);
                    }

                    QuestionBuff+=" ";

                    while (x< characterIndex) {										                    //пока текущая строка не закончится
                        AnswerArr[QuestionNumber][answerIndexChar] = QuestionArr[QuestionNumber][x];	//кладем предлог в массив для ответов посимвольно
                        AnswerBuff+=Character.toString(AnswerArr[QuestionNumber][answerIndexChar]);

                        x++;
                        answerIndexChar++;
                    }

                    AnswerArr[QuestionNumber]=AnswerBuff.toCharArray();										//ставим знак конца строки в конце ответа
                    QuestionArr[QuestionNumber]=QuestionBuff.toCharArray();

                    //System.out.println(AnswerArr[QuestionNumber]);
                }

            }

            if (QuestionArr[QuestionNumber][0] != '\n' && QuestionArr[QuestionNumber][0] !=0 && QuestionArr[QuestionNumber][0]!='*')  //это условие позволяет пропускать пустые строки и строки, начинающиеся с *
                QuestionNumber++;                                                                                                                                   //только при соблюдения этого условия, идет запись следующей строки в массив

            bufferString = reader.readLine();

        }

        Randomize();																					//сразу после созданияя массивов с вопросами и ответами - формируется массив случайных чисел
        //System.out.println("Я СДЕЛЯЛ");
        return 0;
    }
    private int Randomize(){								//метод для созданиия последовательности случайных{															//неповторяющихся чисел
        int arrlength;
        arrlength = QuestionNumber;
        int[] nonReccurentCheckArr = new int[arrlength];									//буферный массив, для чисел, которые уже были использованы (чтобы чтсла не повторялись)
        int[] randomNumbersArr = new int[arrlength];										//массив с конечной последовательностью неповторяющихся случайных чисел

        int RandBuff;																//числовой буфер для сгенерированного числа
        int x=0;

        SecureRandom secureRandom = new SecureRandom();

        RandomNumberArrPointer = randomNumbersArr;									//передаем адрес первого элемента массива в POINT


        for (int i = 0; i < QuestionNumber; i++) {									//буферный массив заполняем числами, которые никак не могут быть индексом массива
            nonReccurentCheckArr[i] = -1;
        }

        for (;;) {																	//цикл работает до тех пор, пока не будет сгенерированно количество чисел равное количеству вопросов в тесте
            RandBuff = secureRandom.nextInt(QuestionNumber-1);

            if (nonReccurentCheckArr[RandBuff] == -1) {								//если свежесгенерированное число еще не использованно, записываем его в массив
                randomNumbersArr[x] = RandBuff;
                nonReccurentCheckArr[RandBuff] = -2;
                x++;
            }

            if (x == QuestionNumber-1) break;

        }
        return 0;
    }
    int PhrasalTesting(){													//основной метод для запуска теста{
        int activeQuestionNumber = 0;														//индекс вопросса/ответа в порядке их выведения
        String activeAnswer;																//строковый буфер для проверки правильности ответа
        String activeQuestion;																//строковый буфер для текущего вопроса
        int typeCommand=TestingType;														//управляющая переменная
        boolean isQuestionRepeated = false;													//переменная для повторяющегося неверного ответа


        for (;;) {
            activeAnswer = String.copyValueOf(AnswerArr[RandomNumberArrPointer[activeQuestionNumber]]);					//в буфер помещаются строки, соответствующие случайному
            activeQuestion = String.copyValueOf(QuestionArr[RandomNumberArrPointer[activeQuestionNumber]]);				//числу, лежащему в массиве случайных числел


            System.out.println(activeQuestion+"___");

            if (typeCommand == 0|| activeQuestionNumber == QuestionNumber) {						//цикл заканчивает работу либо при переборе всех строк из файла
                break;																				//либо если методом проверки возвращен 0
            }

            typeCommand = checkAnswer(getAnswer(), activeAnswer);									//проверка правильного ответа
            //при правильном ответе выводится новая строка
            if (typeCommand == 1) {
                activeQuestionNumber++;
                isQuestionRepeated = false;
            }

            if (typeCommand == 2) {
                ErrorsNumber++;
                if (isQuestionRepeated == false) {													//если ошибочный ответ дан первый раз
                    WrongAnswer[WrongIndex] = RandomNumberArrPointer[activeQuestionNumber];			//в массив кладется номер текущего вопроса и ответа
                    WrongIndex++;
                    isQuestionRepeated = true;
                }


            }

            if (typeCommand == 3) {																			//в случае пропуска вопроса - выводится правильный ответ
                System.out.println(AnswerArr[RandomNumberArrPointer[activeQuestionNumber]]);				//и после выводится новая строка
                ErrorsNumber++;
                MissedQuestions[MissedIndex] = RandomNumberArrPointer[activeQuestionNumber];				//в масив кладется номер текущего вопроса и ответа
                MissedIndex++;
                activeQuestionNumber++;
                isQuestionRepeated = false;
            }
        }

        return 0;
    }

    String getAnswer() {
        String answer;
        Scanner read = new Scanner(System.in);
        answer=read.nextLine();
        return answer;
    }

    int checkAnswer(String answer, String rightAnswer){
//Принимает результат ввода и возвращает 1 при правельном ответе,
//2 при неправильном
//3 при введении команды "next" - для пропуска вопроса и выведения правильного ответа
//и 0 при команде finish
        String commandFinish = "finish";
        String commandNext = "next";

        if (answer.equals(rightAnswer)) {
            System.out.println("Correct\n");
            return 1;
        }

        else if (answer.equals(commandFinish)) {
            return 0;
        }

        else if (answer.equals(commandNext)) {
            return 3;
        }

        else {
            System.out.println("INCORRECT\n");
            return 2;
        }

    }
    public int TestingSummery()
    {
        System.out.println("Всего "+ErrorsNumber+" ошибок  из "+QuestionNumber+" словосочетаний");
        System.out.println("Ошибки в словосочетаниях:\n");

        for (int i = 0; i < WrongIndex; i++) {							//выдает на экран все правильные фразы в которых была допущена ошибка
            System.out.print(QuestionArr[WrongAnswer[i]]);
            System.out.println(AnswerArr[WrongAnswer[i]]);
        }


        System.out.println("\nПропущенные словосочетания:\n");

        for (int x = 0; x < MissedIndex; x++) {							//выдает на экран все фразы, которые были пропущены
            System.out.print(QuestionArr[MissedQuestions[x]]);
            System.out.println(AnswerArr[MissedQuestions[x]]);
        }

        return 0;
    }

}
