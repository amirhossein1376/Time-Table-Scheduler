package core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import moudles.Course;
import moudles.CourseClass;
import moudles.Professor;
import moudles.Schedule;

public class Algorithm {


    public static Algorithm getInstance(Configuration configuration) {
        return new Algorithm(Configuration.POPULATION, Configuration.REPLACE_BY_GENERATION, Configuration.BEST_POPULATION_TRACK_SIZE, configuration);

    }

    private Configuration mConfiguration;
    private int mMaxGeneration = Configuration.MAX_GENERATION;
    private int mCurrentGeneration = 0;
    //number of population replace per generation
    private int mReplaceByGeneration;

    private Schedule[] mPopulation;

    private boolean[] mPopulationBestFlag;

    private int[] mBestPopulationIndexes; // DEC
    private int mBestPopulationCurrentSize;


    public Algorithm(int populationSize, int replaceByGeneration, int bestPopulationSize, Configuration configuration) {
        mConfiguration = configuration;
        mReplaceByGeneration = replaceByGeneration;


        if (replaceByGeneration < 1) {
            replaceByGeneration = 1;
        } else if (mReplaceByGeneration > populationSize - mBestPopulationCurrentSize) {
            mReplaceByGeneration = populationSize - mBestPopulationCurrentSize;
        }

        mPopulation = new Schedule[populationSize];
        mPopulationBestFlag = new boolean[populationSize];
        mBestPopulationIndexes = new int[bestPopulationSize];
    }

    public void startAlgorithm(boolean justHardLimits) {
        initPopulation();
        mCurrentGeneration = 0;
        while (true) {
            Schedule bestChromosome = getBestChromosome();

            if (bestChromosome != null && bestChromosome.getFitness() >= 1000) {
                evaluate(bestChromosome);
                return;
            }

            if (mCurrentGeneration == mMaxGeneration) {
                evaluate(bestChromosome);
                return;
            }

            Schedule[] offspring = new Schedule[mReplaceByGeneration];
            for (int i = 0; i < mReplaceByGeneration; i++) {
                int index1 = 0, index2 = 0;
                do {
                    index1 = (int) (Math.random() * mPopulationBestFlag.length);
                    index2 = (int) (Math.random() * mPopulationBestFlag.length);
                }
                while (mPopulationBestFlag[index1] && mPopulationBestFlag[index2]);

                Schedule p1 = mPopulation[index1];
                Schedule p2 = mPopulation[index2];

                offspring[i] = p1.crossOver(p2);
                offspring[i].mutation();
            }

            for (int i = 0; i < mReplaceByGeneration; i++) {
                int ci = 0;
                do {
                    ci = (int) (Math.random() * mPopulation.length);
                } while (isInBest(ci));

                mPopulation[ci] = offspring[i];

                addToBest(ci);
            }

            mCurrentGeneration++;
        }
    }

    private void evaluate(Schedule schedule) {
        if (schedule == null) {
            schedule = getBestChromosome();
        }

        List<Professor> professors = mConfiguration.getProfessors();
        int[] courses = new int[professors.size()];

        for (CourseClass courseClass : schedule.getClasses()) {
            if (courseClass.getProfessor() != null) {
                courses[courseClass.getProfessor().getProfId()]++;
            }
        }

        for (int i = 0; i < courses.length; i++) {
            courses[i] = courses[i] / 2;
        }

        schedule.calculateFitness();
        float fitness = schedule.getFitness();

        if (fitness < 1000) {
            reDo(schedule);
        } else {
            save(schedule);
        }
    }

    public void reDo(Schedule schedule) {
        Configuration configuration = mConfiguration;
        schedule.calculateFitness(true);
        Set<Course> badCourses = schedule.getBadCourses();
        mConfiguration.getCourses().removeAll(getPart(badCourses));
        Algorithm instance = Algorithm.getInstance(mConfiguration);
        instance.startAlgorithm(true);
    }

    public void save(Schedule schedule) {
        mConfiguration.saveSchedule(schedule);

        Main.results.add(mConfiguration.getCourses().size());
        if (Main.results.size() % 25 == 0)
            mConfiguration.saveResults();

        LogHelper.logInfo("---File               : " + mConfiguration.getDataUtil().mResultFile.getName() + " ---");
        LogHelper.logInfo("---Result Fitness was : " + schedule.getFitness() + " ---");
        LogHelper.logInfo("---Courses            : " + mConfiguration.getCourses().size() + " ---");
        LogHelper.logInfo("---Result copied to file---");
    }

    public boolean equalsZero(int[] a) {
        for (int i : a) {
            if (i != 0) return false;
        }
        return true;
    }

    public List<Course> getPart(Set<Course> courses) {
        int n = (int) Math.ceil(courses.size() / 5);
        List<Course> newCourses = new ArrayList<>();
        Iterator<Course> iterator = courses.iterator();
        int count = 0;
        while (count < n) {
            newCourses.add(iterator.next());
            count++;
        }
        return newCourses;
    }

    public void initPopulation() {
        for (int i = 0; i < mPopulation.length; i++) {
            Schedule schedule = new Schedule(mConfiguration, Configuration.CROSS_OVER_POINTS, Configuration.MUTATION_SIZE);
            schedule.generateRandomSchedule();
            mPopulation[i] = schedule;
            addToBest(i);
        }

        System.out.println("Init Population Done!!");
    }

    private Schedule getBestChromosome() {
        return mPopulation[mBestPopulationIndexes[0]];
    }

    private void addToBest(int scheduleIndex) {
        if (mPopulationBestFlag[scheduleIndex]) {
            return;
        }

        if (mBestPopulationCurrentSize == mBestPopulationIndexes.length && mPopulation[mBestPopulationIndexes[mBestPopulationCurrentSize - 1]].getFitness() >= mPopulation[scheduleIndex].getFitness()) {
            return;
        }

        int i = mBestPopulationCurrentSize;
        for (; i > 0; i--) {
            if (i < mBestPopulationIndexes.length) {
                // group is not full
                if (mPopulation[mBestPopulationIndexes[i - 1]].getFitness() > mPopulation[scheduleIndex].getFitness()) {
                    break;
                }

                mBestPopulationIndexes[i] = mBestPopulationIndexes[i - 1];
            } else {
                // group is full . remove worst chromosome
                mPopulationBestFlag[mBestPopulationIndexes[i - 1]] = false;
            }
        }

        mBestPopulationIndexes[i] = scheduleIndex;
        mPopulationBestFlag[scheduleIndex] = true;

        if (mBestPopulationCurrentSize < mBestPopulationIndexes.length) {
            mBestPopulationCurrentSize++;
        }
    }

    private boolean isInBest(int scheduleIndex) {
        return mPopulationBestFlag[scheduleIndex];
    }
}
