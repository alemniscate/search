package search;

import java.util.*;
import java.util.stream.*;
import java.io.*;

public class Main {
    public static void main(String[] args) {

        Arguments arguments = new Arguments(args);
        String fileName = arguments.get("--data", "text.txt");

        if (!ReadText.isExist(fileName)) {
            return;
        }

        List<String> allPeople = ReadText.readLines(fileName);
        Map<String, List<Integer>> map = new HashMap<>();
        for (int i = 0;i < allPeople.size(); i++) {
            String line = allPeople.get(i);
            String[] words = line.split("\\s+");
            for (String word: words) {
                word = word.toLowerCase();
                if (map.containsKey(word)) {
                    List<Integer> list = map.get(word);
                    list.add(i);
                    map.put(word, list);
                } else {
                    List<Integer> list = new ArrayList<>();
                    list.add(i);
                    map.put(word, list);
                }
            }
        }

        Scanner scanner = new Scanner(System.in);
        Action ac = new Action(scanner, allPeople, map);

        while (true) {
            System.out.println("=== Menu ===");
            System.out.println("1. Find a person");
            System.out.println("2. Print all people");
            System.out.println("0. Exit");
            int menuno = Integer.parseInt(scanner.nextLine());
            System.out.println();
            switch (menuno) {
                case 1:
                    ac.find();
                    break;
                case 2:
                    ac.printAll();
                    break;
                case 0:
                    ac.exit();
                    break;
                default:
                    System.out.println("Incorrect option! Try again.");
                    break;

            }
            if (menuno == 0) {
                break;
            }
            System.out.println();
        }
        scanner.close();
    }
}

class Action {

    Scanner scanner;
    List<String> allPeople;
    Map<String, List<Integer>> map;

    Action(Scanner scanner, List<String> allPeople, Map<String, List<Integer>> map) {
        this.scanner = scanner;
        this.allPeople = allPeople;
        this.map = map;
    }

    void find() {
        System.out.println("Select a matching strategy: ALL, ANY, NONE");
        String strategy = scanner.nextLine().toUpperCase();
        System.out.println();
        System.out.println("Enter a name or email to search all suitable people.");
        String input = scanner.nextLine().toLowerCase();
        System.out.println();
        String[] words = input.split("\\s+");
        Set<Integer> set = new HashSet<>();
        switch (strategy) {
            case "ALL":
                set = findAll(words);
                break;
            case "ANY":
                set = findAny(words);
                break;
            case "NONE":
                set = findNone(words);
                break;
        }
        if (!set.isEmpty()) {
            System.out.println(String.format("%d persons found:", set.size()));
            set.stream().forEach(i -> System.out.println(allPeople.get(i)));
        } else {
            System.out.println("No matching people found.");
        }
    }

    Set<Integer> findAll(String[] words) {
        Set<Integer> set = new HashSet<>();
        for (String word: words) {
            if (map.containsKey(word)) {
                Set<Integer> set1 = map.get(word).stream().collect(Collectors.toSet());
                if (set.isEmpty()) {
                    set.addAll(set1);
                } else {
                    set.retainAll(set1);
                }
            }
        }
        return set;
    } 

    Set<Integer> findAny(String[] words) {
        Set<Integer> set = new HashSet<>();
        for (String word: words) {
            if (map.containsKey(word)) {
                for (int i: map.get(word)) {
                    set.add(i);
                }
            }
        }
        return set;
    }

    Set<Integer> findNone(String[] words) {
        Set<Integer> set = new HashSet<>();
        IntStream.range(0, allPeople.size()).forEach(i -> set.add(i));
        set.removeAll(findAny(words));
        return set;
    }

    void printAll() {
        System.out.println("=== List of people ===");
        allPeople.stream().forEach(System.out::println);
    }

    void exit() {
        System.out.println("Bye!");
    }
}

class Arguments {

    Map<String, String> argMap;

    Arguments(String[] args) {
        
        List<String> argList = Arrays.asList(args);
        argMap = new HashMap<>();
        for (int i = 0; i < args.length; i += 2) {
            argMap.put(argList.get(i), argList.get(i + 1));
        }
    }

    String get(String key, String defaultValue) {
        if (argMap.isEmpty()) {
            return defaultValue;
        }

        if (argMap.get(key) == null) {
            return defaultValue;
        }
        
        return argMap.get(key);
    }
}

class ReadText {

    static boolean isExist(String fileName) {
        File file = new File(fileName);
        if (file.exists()) {
            return true;
        } else {
            return false;
        }
    }

    static String getAbsolutePath(String fileName) {
        File file = new File(fileName);
        return file.getAbsolutePath();
    }

    static String readAllWithoutEol(String fileName) {
        String text = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));   
            text =  br.lines().collect(Collectors.joining());        
            br.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return text;
    }

    static List<String> readLines(String fileName) {
        List<String> lines = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));   
            lines =  br.lines().collect(Collectors.toList());        
            br.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return lines;
    }

    static String readAll(String fileName) {
        char[] cbuf = new char[4096];
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));           
            while (true) {
                int length = br.read(cbuf, 0, cbuf.length);
                if (length != -1) {
                    sb.append(cbuf, 0, length);
                }
                if (length < cbuf.length) {
                    break;
                }
            }
            br.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return sb.toString();
    }
}
