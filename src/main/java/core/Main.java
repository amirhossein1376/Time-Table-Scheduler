package core;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    private static ExecutorService mExecutorService = Executors.newFixedThreadPool(3);
    public static List<Integer> results = new ArrayList<Integer>();

    public static void main(String[] args) {
        doWork("C:\\AI\\project\\LAST_PH\\");

    }

    private static void doWork(String filesPath) {
        File freeClassesFolder = new File(filesPath + "FreeClass");
        File profsFreeTimeFolder = new File(filesPath + "ProfFreeTime");
        File profSkillFolder = new File(filesPath + "SKILL");
        File capacityFolder = new File(filesPath + "capacity");
        File askedClassesFolder = new File(filesPath + "AskedClass");
        File chartFile = new File(filesPath + "Chart").listFiles()[0];
        File fitnessFile = new File(filesPath + "results.txt");

        File[] freeClasses = freeClassesFolder.listFiles();
        File[] freeTimes = profsFreeTimeFolder.listFiles();
        File[] skills = profSkillFolder.listFiles();
        File[] capacities = capacityFolder.listFiles();
        File[] askedClasses = askedClassesFolder.listFiles();

        for (File freeClassFile : freeClasses) {
            if (freeClassFile.getName().contains("$")) continue;
            for (File freeTimeFile : freeTimes) {
                if (freeTimeFile.getName().contains("$")) continue;
                c:
                for (File skillFile : skills) {
                    if (skillFile.getName().contains("$")) continue;
                    for (File askedClassFile : askedClasses) {
                        if (askedClassFile.getName().contains("$")) continue;
                        for (File capacityFile : capacities) {
                            if (capacityFile.getName().contains("$")) continue;

                            String skillFileName = skillFile.getName().replace(".xlsx", "").replace("profskill", "").replace("_profnumber", "");
                            String freeTimeFileName = freeTimeFile.getName().replace(".xlsx", "").replace("prof_freetime", "").replace("_profnumber", "");
                            String freeClassFileName = freeClassFile.getName().replace(".xlsx", "").replace("Freeclass", "");
                            String registerFileName = askedClassFile.getName().replace(".xlsx", "").replace("register", "");
                            String capacityFileName = capacityFile.getName().replace(".xlsx", "").replace("class_capacity", "");

                            StringTokenizer skillFileNameTokenizer = new StringTokenizer(skillFileName, "-");
                            int skillFileNum = Integer.parseInt(skillFileNameTokenizer.nextToken());
                            int skillFileProfCount = Integer.parseInt(skillFileNameTokenizer.nextToken());

                            StringTokenizer freeTimeFileNameTokenizer = new StringTokenizer(freeTimeFileName, "-");
                            int freeTimeFileNum = Integer.parseInt(freeTimeFileNameTokenizer.nextToken());
                            int freeTimeFileProfCount = Integer.parseInt(freeTimeFileNameTokenizer.nextToken());

                            int freeClassFileNum = Integer.parseInt(freeClassFileName);
                            int registerFileNum = Integer.parseInt(registerFileName);
                            int capacityFileNum = Integer.parseInt(capacityFileName);

                            if (freeTimeFileProfCount != skillFileProfCount) {
                                continue c;
                            }

                            String name = filesPath + "Result\\result_" + skillFileNum + "_" + freeTimeFileNum + "_" + freeClassFileNum + "_" + capacityFileNum + "_" + registerFileNum;
                            File resultFile = new File(name + ".xlsx");
                            File textFile = new File(name + ".txt");

                            DataUtil dataUtil = new DataUtil(freeClassFile, freeTimeFile, skillFile, capacityFile, askedClassFile, chartFile, resultFile, textFile, fitnessFile);
                            final Configuration configuration = new Configuration(dataUtil);

                            mExecutorService.execute(new Runnable() {
                                @Override
                                public void run() {
                                    Algorithm algorithm = Algorithm.getInstance(configuration);
                                    algorithm.startAlgorithm(false);
                                }
                            });
                        }
                    }
                }
            }
        }
    }
}