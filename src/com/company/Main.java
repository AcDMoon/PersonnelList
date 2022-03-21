package com.company;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.*;


public class Main {

    private static ArrayList<Workers> workers = new ArrayList<>(); // Коллекция для хранения работников
    private static ArrayList<Managers> managers = new ArrayList<>(); // Коллекция для хранения менеджеров
    private static ArrayList<Others> others = new ArrayList<>(); // Коллекция для хранения других


    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException, TransformerException {


        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(); // Получение фабрики, чтобы после получить билдер документов.


        try {//Пробуем запарсить файл, иначе создадим его и потом будем добавлять.

            DocumentBuilder builder = factory.newDocumentBuilder(); // Получили из фабрики билдер, который парсит XML, создает структуру Document в виде иерархического дерева.

            Document document = builder.parse(new File("Personnel.xml"));// Запарсили XML, создав структуру Document. Теперь у нас есть доступ ко всем элементам, каким нам нужно.

            collectInformation(document); // функция, которая помещает людей (из существующего XML) в соответствующие классы

            workers.forEach(System.out::println);
            managers.forEach(System.out::println);
            others.forEach(System.out::println);


        } catch (FileNotFoundException e) {
            }

        final String DOCEMENTATION ="Список комманд:\n\n" +
                "-  AddPersonnel  ---> Добавляет сотрудника(ов) в список сотрудников\n" +
                "-  DeletePersonnel  ---> Удаляет сотрудника(ов) из списка сотрудников\n" +
                "-  ChangeType  ---> Меняет тип сотрудника на указанный\n" +
                "-  ManagerAdd  ---> Привязывает сотрудника к менеджеру\n" +
                "-  SortSecondName ---> Сортирует сотрудников по фамилии\n" +
                "-  SortTurn ---> Сортирует сотрудников по дате начала работы";
        System.out.println(DOCEMENTATION);

        CommandScanner();

    }



    private static void collectInformation(Document document) {

        Node root = document.getDocumentElement();
        NodeList PERSONNELS = root.getChildNodes();

        for (int i = 0; i < PERSONNELS.getLength(); i++) {
            Node workerType = PERSONNELS.item(i);
            if (workerType.getNodeType() != Node.TEXT_NODE) {
                NodeList elements = workerType.getChildNodes();
                for (int e = 0; e < elements.getLength(); e++) {
                    Node human = elements.item(e);
                    if (human.getNodeType() != Node.TEXT_NODE) {
                        //System.out.println(elem); // нода конкретного работника
                        //System.out.println(elem.getNodeName()); // должность работника
                        NamedNodeMap attribute = human.getAttributes();
                        //System.out.println(attribute.getNamedItem("fullName").getNodeValue()); // атрибут работника (в данном случае fullname)
                        String employeePosition = human.getNodeName();
                        String fullName = attribute.getNamedItem("fullName").getNodeValue();
                        String DOB = attribute.getNamedItem("DOB").getNodeValue();
                        String turn_to = attribute.getNamedItem("turn-to").getNodeValue();


                        // В зависимости от типа элемента, нам нужно собрать свою дополнительну информацию про каждый подкласс, а после добавить нужные образцы в коллекцию.
                        switch (employeePosition) {
                            case "worker": {

                                workers.add(new Workers(employeePosition, fullName, DOB, turn_to));
                            }
                            break;
                            case "manager": {
                                String workerList = attribute.getNamedItem("workerList").getNodeValue();

                                managers.add(new Managers(employeePosition, fullName, DOB, turn_to, workerList));
                            }
                            break;
                            default: {
                                String textDescription = attribute.getNamedItem("textDescription").getNodeValue();

                                others.add(new Others(employeePosition, fullName, DOB, turn_to, textDescription));
                            }
                            break;
                        }}}}}}






    private static void CommandScanner() throws IOException, ParserConfigurationException, TransformerException {
        System.out.println("Введите комманду:");
        Scanner in = new Scanner(System.in);
        String command = in.nextLine();

        switch (command) {
            case "AddPersonnel": {
                AddPersonnel();
            }
            break;
            case "DeletePersonnel": {
                DeletePersonnel();
            }
            break;
            case "ChangeType": {
                ChangeType();
            }
            break;
            case "ManagerAdd": {
                ManagerAdd();
            }
            break;
            case "SortSecondName": {
                SortSecondName();
            }
            break;
            case "SortTurn": {
                SortTurn();
            }
            break;
            default:{
                System.out.println("Неизвестная комманда, проверьте правильность написание комманды");
            }
            break;
        }
        CommandScanner();
    }


    private static void XMLCreator() throws ParserConfigurationException, TransformerException {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        Document doc = factory.newDocumentBuilder().newDocument();

        Element Personnel = doc.createElement("Personnel");
        doc.appendChild(Personnel);



        Element Workers = doc.createElement("workers");
        Personnel.appendChild(Workers);

        for (Workers w : workers) {
            Element worker = doc.createElement(w.get_employeePosition());
            worker.setAttribute("fullName", w.get_fullName());
            worker.setAttribute("DOB", w.get_DOB());
            worker.setAttribute("turn-to", w.get_turn_to());
            Workers.appendChild(worker);
        }


        Element Managers = doc.createElement("managers");
        Personnel.appendChild(Managers);

        for (Managers m : managers) {
            Element manager = doc.createElement(m.get_employeePosition());
            manager.setAttribute("fullName", m.get_fullName());
            manager.setAttribute("DOB", m.get_DOB());
            manager.setAttribute("turn-to", m.get_turn_to());
            manager.setAttribute("workerList", m.get_workerList());
            Managers.appendChild(manager);
        }



        Element Others = doc.createElement("others");
        Personnel.appendChild(Others);

        for (Others o : others) {
            String pos = o.get_employeePosition().replace(" ", "_");
            Element secretary = doc.createElement(pos);
            secretary.setAttribute("fullName", o.get_fullName());
            secretary.setAttribute("DOB", o.get_DOB());
            secretary.setAttribute("turn-to", o.get_turn_to());
            secretary.setAttribute("textDescription", o.get_textDescription());
            Others.appendChild(secretary);
        }



        File file = new File("Personnel.xml");

        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.transform(new DOMSource(doc), new StreamResult(file));

    }




    private static void AddPersonnel() throws IOException, ParserConfigurationException, TransformerException {
        System.out.println("Введите путь к текстовому файлу с сотрудниками:");
        Scanner in = new Scanner(System.in);
        String path = in.nextLine();


        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(path));
        } catch (FileNotFoundException e) {
            System.out.println("Такого файла не существует!");
            return;
        }


        String str; // строка из текстового докумета

        ArrayList<String> list = new ArrayList<String>(); // создание списка

        while ((str = reader.readLine()) != null) { // цикл перебора пока строки не закончатся
            if (!str.isEmpty()) { // если строка не пустая
                list.add(str); // добавляет её в список
            }}

        for (int i = 1; i < list.size(); i++) {
            List<String> asList = Arrays.asList(list.get(i).split("  ")); // list.get(0) - получение элемента по индексу, остальное - преобразование строки в список
            boolean repeat = false;
            switch (asList.get(0)) {

                case "worker": {
                    for (Workers w : workers) {
                        if (w.get_fullName().equals(asList.get(1))) {
                            repeat = true;
                            System.out.println(asList.get(1) + " <-- Уже есть в списке");
                            break;
                        }}
                    if (repeat == false) {
                        workers.add(new Workers(asList.get(0), asList.get(1), asList.get(2), asList.get(3)));
                    }}
                break;

                case "manager": {
                    for (Managers m : managers) {
                        if (m.get_fullName().equals(asList.get(1))) {
                            repeat = true;
                            System.out.println(asList.get(1) + " <-- Уже есть в списке");
                            break;
                        }}
                    if (repeat == false) {
                        managers.add(new Managers(asList.get(0), asList.get(1), asList.get(2), asList.get(3), asList.get(4)));
                    }}
                break;

                default: {
                    for (Others o : others) {
                        if (o.get_fullName().equals(asList.get(1))) {
                            repeat = true;
                            System.out.println(asList.get(1) + " <-- Уже есть в списке");
                            break;
                        }}
                    if (repeat == false) {
                        others.add(new Others(asList.get(0), asList.get(1), asList.get(2), asList.get(3), asList.get(5)));
                    }}
                break;
            }}

        XMLCreator();
        System.out.println("\nРаботники успешно добавлены в файл Personnel.txt");
    }


    private static void DeletePersonnel() throws ParserConfigurationException, TransformerException {
        System.out.println("Введите должность сотрудника (работник/менеджер/другое):");
        Scanner in = new Scanner(System.in);
        String type = in.nextLine();

        boolean notfound = true;

        switch (type) {
            case "работник":{
                System.out.println("Введите ФИО сотрудника:");
                String name = in.nextLine();

                for (Workers w : workers) {
                    if (name.equals(w.get_fullName())){
                        notfound = false;
                        workers.remove(w);
                        System.out.println("Работник " + name + " успешно удалён!");
                        break;
                    }}
                if (notfound){System.out.println("Работник " + name + " не найден!");}
            }
            break;

            case "менеджер":{
                System.out.println("Введите ФИО сотрудника:");
                String name = in.nextLine();

                for (Managers m : managers) {
                    if (name.equals(m.get_fullName())){
                        notfound = false;
                        managers.remove(m);
                        System.out.println("Работник " + name + " успешно удалён!");
                        break;
                    }}
                if (notfound){System.out.println("Работник " + name + " не найден!");}
            }
            break;

            default:{
                System.out.println("Введите ФИО сотрудника:");
                String name = in.nextLine();

                for (Others o : others) {
                    if (name.equals(o.get_fullName())){
                        notfound = false;
                        others.remove(o);
                        System.out.println("Работник " + name + " успешно удалён!");
                        break;
                    }}
                if (notfound){System.out.println("Работник " + name + " не найден!");}
            }
            break;
        }


        XMLCreator();
        workers.forEach(System.out::println);
    }


    private static void ChangeType() throws ParserConfigurationException, TransformerException {
        System.out.println("Введите должность сотрудника (работник/менеджер/другое):");
        Scanner in = new Scanner(System.in);
        String type = in.nextLine();

        System.out.println("Введите ФИО сотрудника:");
        String name = in.nextLine();

        System.out.println("Введите должность на которую хотите назначить сотрудника (работник/менеджер/другое):");//
        String newtype = in.nextLine();

        boolean notfound = true;

        switch (type) {
            case "работник":{

                for (Workers w : workers) { //ищем работника
                    if (name.equals(w.get_fullName())){
                        notfound = false;

                        switch (newtype){
                            case "работник":{
                                System.out.println("Данный работник уже имеет эту должность!");
                            }
                            break;

                            case "менеджер":{
                                boolean repeat = false;
                                for (Managers m : managers){
                                    if (m.get_fullName().equals(name)){
                                        repeat = true;
                                        break;
                                    }}
                                if (!repeat) {
                                    managers.add(new Managers("manager", w.get_fullName(),w.get_DOB(),w.get_turn_to(),""));
                                    workers.remove(w);
                                }else{System.out.println("Работник " + name + " Уже занимает эту должность");}
                            }
                            break;

                            default:{
                                boolean repeat = false;
                                for (Others o : others){
                                    if (o.get_fullName().equals(name)){
                                        repeat = true;
                                        break;
                                    }}
                                if (!repeat) {
                                    System.out.println("Введите текстовое описание:");
                                    String textDescription = in.nextLine();

                                    others.add(new Others(newtype, w.get_fullName(),w.get_DOB(),w.get_turn_to(),textDescription));
                                    workers.remove(w);
                                }else{System.out.println("Работник " + name + " Уже занимает эту должность");}
                            }
                            break;
                            }
                        break;
                        }
                    }
                if (notfound){System.out.println("Работник " + name + " не найден!");}
            }
            break;


            case "менеджер":{

                for (Managers m : managers) { //ищем работника
                    if (name.equals(m.get_fullName())){
                        notfound = false;

                        switch (newtype){
                            case "работник":{
                                boolean repeat = false;
                                for (Workers w : workers){
                                    if (w.get_fullName().equals(name)){
                                        repeat = true;
                                        break;
                                    }}
                                if (!repeat) {
                                    workers.add(new Workers("worker", m.get_fullName(),m.get_DOB(),m.get_turn_to()));
                                    managers.remove(m);
                                }else{System.out.println("Работник " + name + " Уже занимает эту должность");}
                            }
                            break;

                            case "менеджер":{
                                System.out.println("Данный работник уже имеет эту должность!");
                            }
                            break;

                            default:{
                                boolean repeat = false;
                                for (Others o : others){
                                    if (o.get_fullName().equals(name)){
                                        repeat = true;
                                        break;
                                    }}
                                if (!repeat) {
                                    System.out.println("Введите тексто описание:");
                                    String textDescription = in.nextLine();
                                    others.add(new Others(newtype, m.get_fullName(),m.get_DOB(),m.get_turn_to(),textDescription));
                                    managers.remove(m);
                                }else{System.out.println("Работник " + name + " Уже занимает эту должность");}
                            }
                            break;
                        }
                        break;
                    }
                }
                if (notfound){System.out.println("Работник " + name + " не найден!");}
            }
            break;


            default:{

                for (Others o : others) { //ищем работника
                    if (name.equals(o.get_fullName())){
                        notfound = false;

                        switch (newtype){
                            case "работник":{
                                boolean repeat = false;
                                for (Workers w : workers){
                                    if (w.get_fullName().equals(name)){
                                        repeat = true;
                                        break;
                                    }}
                                if (!repeat) {
                                    workers.add(new Workers("worker", o.get_fullName(),o.get_DOB(),o.get_turn_to()));
                                    others.remove(o);
                                }else{System.out.println("Работник " + name + " Уже занимает эту должность");}
                            }
                            break;

                            case "менеджер":{
                                boolean repeat = false;
                                for (Managers m : managers){
                                    if (m.get_fullName().equals(name)){
                                        repeat = true;
                                        break;
                                    }}
                                if (!repeat) {
                                    managers.add(new Managers("manager", o.get_fullName(),o.get_DOB(),o.get_turn_to(),""));
                                    others.remove(o);
                                }else{System.out.println("Работник " + name + " Уже занимает эту должность");}
                            }
                            break;

                            default:{
                                System.out.println("Данный работник уже имеет эту должность!");
                            }
                            break;
                        }
                        break;
                    }
                }
                if (notfound){System.out.println("Работник " + name + " не найден!");}
            }
            break;
        }


        XMLCreator();

    }


    private static void ManagerAdd() throws ParserConfigurationException, TransformerException {
        System.out.println("Введите ФИО сотрудника:");
        Scanner in = new Scanner(System.in);
        String name = in.nextLine();

        boolean notFoundManager = true;


        for (Managers m : managers) { //ищем менеджера
            if (name.equals(m.get_fullName())) {
                notFoundManager = false;

                System.out.println("Введите ФИО сотрудника(ов) которых хотите привязать (через запятую):");
                String workerName = in.nextLine();
                List<String> asList = Arrays.asList(workerName.split(","));


                for (String s : asList) {
                    boolean notFoundWorker = true;
                    for (Workers w : workers) {//ищем работников
                        if (s.trim().equals(w.get_fullName())) {
                            notFoundWorker = false;
                            if (m.get_workerList().contains(s.trim())){
                                System.out.println("Работник " + s.trim() + " уже привязан к менеджеру!");
                            }else{
                                m.set_workerList(m.get_workerList() + s.trim() + ',');
                                System.out.println("Работник " + s.trim() + " успешно добавлен к менеджеру name!");
                            }
                            break;
                        }
                    }
                    if (notFoundWorker) {
                        System.out.println("Работник " + s.trim() + " не найден!");
                    }
                }
                break;
            }
        }
        if (notFoundManager) {
            System.out.println("Работник " + name + " не найден!");
        }

        XMLCreator();
    }


    private static void SortSecondName() throws ParserConfigurationException, TransformerException {
        workers.sort((o1,o2) -> o1.get_fullName().compareTo(o2.get_fullName()));
        managers.sort((o1,o2) -> o1.get_fullName().compareTo(o2.get_fullName()));
        others.sort((o1,o2) -> o1.get_fullName().compareTo(o2.get_fullName()));
        XMLCreator();
        System.out.println("XML успешно отсортирован по фамилиям");
    }


    private static void SortTurn() throws ParserConfigurationException, TransformerException {

        workers.sort((o1, o2) -> {
            if (o1.get_year().equals(o2.get_year()))
                if (o1.get_month().equals(o2.get_month()))
                    return o1.get_day() - o2.get_day();
                else return o1.get_month() - o2.get_month();
            else return o1.get_year() - o2.get_year();
        });

        managers.sort((o1, o2) -> {
            if (o1.get_year() == o2.get_year())
                if (o1.get_month() == o2.get_month())
                    return o1.get_day() - o2.get_day();
                else return o1.get_month() - o2.get_month();
            else return o1.get_year() - o2.get_year();
        });

        others.sort((o1, o2) -> {
            if (o1.get_year() == o2.get_year())
                if (o1.get_month() == o2.get_month())
                    return o1.get_day() - o2.get_day();
                else return o1.get_month() - o2.get_month();
            else return o1.get_year() - o2.get_year();
        });

        XMLCreator();

        System.out.println("XML успешно отсортирован по началу работы");


    }




//нет функции дампа xml-файла на случай непредвиденной остановки программы (может начать строить новый xml и если его прервать он выплюнет пустой, не оставив старые данные)






}





























