package moudles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import core.Configuration;
import core.Triplet;

public class Schedule {

    public static final int WORKING_DAYS_NUM = CourseClass.DAYS.length;
    public static final int DAY_SLOTS = CourseClass.TIMES.length;
    public int[] Ha;
    public int[] Sa;
    private Configuration mConfiguration;

    private List<List<List<CourseClass>>> mSlots;
    private List<CourseClass> mClasses;
    private float mFitness = 0;

    private float mNumberOfCrossoverPoints;
    private int mMutationSize;

    private int mCrossOverProbability = Configuration.CROSS_OVER_PROBABILITY;
    private int mMutationProbability = Configuration.MUTATION_PROBABILITY;
    private Set<Course> mBadCourses = new HashSet<>();

    public Schedule(Configuration configuration, int numberOfCrossoverPoints, int mutationSize) {
        mConfiguration = configuration;
        mNumberOfCrossoverPoints = numberOfCrossoverPoints;
        mMutationSize = mutationSize;

        initSlots();
    }

    private void initSlots() {
        mSlots = new ArrayList<List<List<CourseClass>>>();
        for (int i = 0; i < WORKING_DAYS_NUM; i++) {
            mSlots.add(new ArrayList<List<CourseClass>>());
            for (int j = 0; j < DAY_SLOTS; j++) {
                mSlots.get(i).add(new ArrayList<CourseClass>());
                for (int k = 0; k < mConfiguration.getNumberOfRooms(); k++) {
                    mSlots.get(i).get(j).add(null);
                }
            }
        }

        mClasses = new ArrayList<>();
    }

    public Schedule(Schedule schedule, boolean setupOnly) {
        mConfiguration = schedule.getConfiguration();
        mNumberOfCrossoverPoints = schedule.getNumberOfCrossoverPoints();
        mMutationSize = schedule.getMutationSize();
        mCrossOverProbability = schedule.getCrossOverProbability();
        mMutationProbability = schedule.getMutationProbability();

        if (!setupOnly) {
            mSlots = schedule.getSlots();
            mFitness = schedule.mFitness;
            mClasses = schedule.getClasses();
        } else {
            initSlots();
        }

    }

    private Configuration getConfiguration() {
        return mConfiguration;
    }

    public Schedule crossOver(Schedule parent) {

        if (mClasses == null)
            return parent;
        if (parent.getClasses() == null)
            return this;

        double rand = Math.random() * 100;
        if (rand > mCrossOverProbability) {
            // no cross over
            return new Schedule(this, false);
        }

        Schedule schedule = new Schedule(this, true);

        int size = mClasses.size();

        boolean[] crossOverPointsInClasses = new boolean[size];

        for (int i = 0; i < mNumberOfCrossoverPoints; i++) {
            while (true) {
                int randomIndex = (int) (Math.random() * size);
                if (!crossOverPointsInClasses[randomIndex]) {
                    crossOverPointsInClasses[randomIndex] = true;
                    break;
                }
            }
        }


        Iterator<CourseClass> firstIterator = mClasses.iterator();
        Iterator<CourseClass> parentIterator = parent.getClasses().iterator();

        boolean first = (rand * 2 == 0);
        for (int i = 0; i < size; i++) {

            CourseClass next1 = firstIterator.next();
            CourseClass next2 = parentIterator.next();

            if (first) {
                schedule.findFreeSlotForClass(next1);

            } else {
                schedule.findFreeSlotForClass(next2);
            }

            if (crossOverPointsInClasses[i]) {
                // change source chromosome
                first = !first;
            }
        }

        schedule.calculateFitness();

        return schedule;
    }

    private void findFreeSlotForClass(CourseClass courseClass) {
        int randI = 0;
        int randJ = 0;
        int randK = 0;
        int count = WORKING_DAYS_NUM * DAY_SLOTS * mConfiguration.getNumberOfRooms();
        do {
            randI = (int) (Math.random() * WORKING_DAYS_NUM);
            randJ = (int) (Math.random() * DAY_SLOTS);
            randK = (int) (Math.random() * mConfiguration.getNumberOfRooms());
            count--;
        } while (mSlots.get(randI).get(randJ).get(randK) != null && count > 0);

        CourseClass courseClass1 = new CourseClass();
        courseClass1.setCourse(courseClass.getCourse());
        courseClass1.setProfessor(courseClass.getProfessor());
        courseClass1.setDay(CourseClass.DAYS[randI]);
        courseClass1.setTime(CourseClass.TIMES[randJ]);
        courseClass1.setRoom(mConfiguration.getRooms().get(randK));
        mSlots.get(randI).get(randJ).set(randK, courseClass1);
        mClasses.add(courseClass1);
    }

    public void mutation() {

        double rand = Math.random() * 100;
        if (rand > mMutationProbability) {
            //no mutation
            return;
        }

        if (mClasses == null) return;

        int numberOfClasses = mClasses.size();

        for (int i = 0; i < mMutationSize; i++) {

            int currentCourseClassPos = (int) (Math.random() * numberOfClasses);
            CourseClass currentCourse = mClasses.get(currentCourseClassPos);

            while (true) {
                int replacingI = (int) (Math.random() * WORKING_DAYS_NUM);
                int replacingJ = (int) (Math.random() * DAY_SLOTS);
                int replacingK = (int) (Math.random() * mConfiguration.getNumberOfRooms());
                if (mSlots.get(replacingI).get(replacingJ).get(replacingK) == null) {
                    currentCourse.setRoom(mConfiguration.getRooms().get(replacingK));
                    mSlots.get(replacingI).get(replacingJ).set(replacingK, currentCourse);
                    removeCourseClass(currentCourse);
                    break;
                }
            }
        }

        calculateFitness();
    }

    public Set<Course> getBadCourses() {
        return mBadCourses;
    }

    public void calculateFitness() {
        calculateFitness(false);
    }

    public void calculateFitness(boolean initBadCourses) {

        if (initBadCourses) {
            mBadCourses.clear();
        }

        int score = 0;
        int numberOfRooms = mConfiguration.getNumberOfRooms();
        int daySize = DAY_SLOTS * numberOfRooms;

        Ha = new int[Configuration.HARD_LIMITATIONS];
        Sa = new int[Configuration.SOFT_LIMITATIONS]; // تعداد دفعات نقض محدودیت

        int[] H = {1, 1, 1, 1, 1};  // وزن محدودیت
        int[] S = {1, 1};

        for (CourseClass courseClass : mClasses) {

            boolean bad = false;
            if (checkRoomOverLapping(courseClass)) {
                Ha[0]++;
                bad = true;
            }

            if (checkCourseOverLapping(courseClass)) {
                Ha[1]++;
                bad = true;
            }

            if (checkProfessorOverLapping(courseClass)) {
                Ha[2]++;
                bad = true;
            }

            if (!isClassProgramOk(courseClass)) {
                Ha[3]++;
                bad = true;
            }

            float a = classCapacityRate(courseClass) / (float) 2;

            Ha[4] += a;
            if (a > 0) {
                bad = true;
            }

            if (bad && initBadCourses) {
                mBadCourses.add(courseClass.getCourse());
            }

            if (!isProfessorProgramOk(courseClass)) {
                Sa[0]++;
            }

            if (!isChartOk(courseClass)) {
                Sa[1]++;
            }
        }

        float sigmaH = 0;
        for (int i = 0; i < Ha.length; i++) {
            sigmaH += Ha[i] * H[i];
        }

        float sigmaS = 0;
        for (int i = 0; i < Sa.length; i++) {
            sigmaS += Sa[i] * S[i];
        }

        mFitness = Configuration.K_Ratio / (0.8f * sigmaH + 0.2f * sigmaS + 1);
    }

    public int getProfessorProgramConflict() {
        return Sa[0];
    }

    public void generateRandomSchedule() {
        List<Course> courses = mConfiguration.getCourses();

        int courseCounter = 0;
        for (Course course : courses) {

            Professor professorForCourse = findProfessorForCourse(courseCounter);

            if (professorForCourse == null) {
                continue;
            }

            CourseClass courseClass1 = new CourseClass();
            courseClass1.setCourse(course);
            courseClass1.setProfessor(professorForCourse);
            findSlotForCourseWithProfessor(courseClass1);
            mClasses.add(courseClass1);

            if (course.getName().charAt(course.getName().length() - 1) >= '3') {
                CourseClass courseClass2 = new CourseClass();
                courseClass2.setCourse(course);
                courseClass2.setProfessor(professorForCourse);
                findSlotForCourseWithProfessor(courseClass2);
                mClasses.add(courseClass2);
            }

            courseCounter++;
        }

        calculateFitness();
    }

    private Professor findProfessorForCourse(int courseCounter) {
        List<Professor> professors = mConfiguration.getProfessors();
        List<Integer> rate = new ArrayList<>(professors.size());
        int profIndex;
        int count = professors.size() * 2;
        do {
            profIndex = (int) (Math.random() * professors.size());
            count--;
        } while (professors.get(profIndex).getProfSkills()[courseCounter] == 0 && count > 0);

        return professors.get(profIndex);
    }

    private void findSlotForCourseWithProfessor(CourseClass courseClass) {
        int numberOfRooms = mConfiguration.getNumberOfRooms();
        List<Room> rooms = mConfiguration.getRooms();

        Map<Triplet<Integer, Integer, Integer>, Float> map = new HashMap<>(); // index and score

        int count = WORKING_DAYS_NUM * DAY_SLOTS * 3;
        while (count > 0) {

            int dayIndex = (int) (Math.random() * WORKING_DAYS_NUM);
            int timeIndex = (int) (Math.random() * DAY_SLOTS);
            int roomIndex = (int) (Math.random() * numberOfRooms);

            courseClass.setDay(CourseClass.DAYS[dayIndex]);
            courseClass.setTime(CourseClass.TIMES[timeIndex]);
            courseClass.setRoom(rooms.get(roomIndex));

            map.put(new Triplet<Integer, Integer, Integer>(dayIndex, timeIndex, roomIndex), calculateViolationOfRestrictions(courseClass));

            count--;
        }

        map = sortByValue(map);

        Map.Entry<Triplet<Integer, Integer, Integer>, Float> zeroIndex = getFirstElement(map.entrySet());

        int dayIndex = zeroIndex.getKey().getFirst();
        int timeIndex = zeroIndex.getKey().getSecond();
        int roomIndex = zeroIndex.getKey().getThird();

        courseClass.setDay(CourseClass.DAYS[dayIndex]);
        courseClass.setTime(CourseClass.TIMES[timeIndex]);
        courseClass.setRoom(rooms.get(roomIndex));

        mSlots.get(dayIndex).get(timeIndex).set(roomIndex, courseClass);
    }

    private float calculateViolationOfRestrictions(CourseClass courseClass) {
        float violationOfRestrictions = 0;

        if (checkRoomOverLapping(courseClass)) {
            violationOfRestrictions += 1;
        }

        if (checkCourseOverLapping(courseClass)) {
            violationOfRestrictions += 1;
        }

        if (checkProfessorOverLapping(courseClass)) {
            violationOfRestrictions += 1;
        }

        if (!isClassProgramOk(courseClass)) {
            violationOfRestrictions += 1;
        }

        if (!isProfessorProgramOk(courseClass)) {
            violationOfRestrictions += 1;
        }


        if (!isChartOk(courseClass)) {
            violationOfRestrictions += 1;
        }

        violationOfRestrictions += classCapacityRate(courseClass) / (float) 2;
        return violationOfRestrictions;
    }

    public static <T> T getFirstElement(final Iterable<T> elements) {
        if (elements == null)
            return null;

        return elements.iterator().next();
    }

    public static <T> T getLastElement(final Iterable<T> elements) {
        final Iterator<T> itr = elements.iterator();
        T lastElement = itr.next();

        while (itr.hasNext()) {
            lastElement = itr.next();
        }

        return lastElement;
    }

    public static <K, V> Map<K, V> sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new LinkedList<>(map.entrySet());
        Collections.sort(list, new Comparator<Object>() {
            @SuppressWarnings("unchecked")
            public int compare(Object o1, Object o2) {
                return ((Comparable<V>) ((Map.Entry<K, V>) (o1)).getValue()).compareTo(((Map.Entry<K, V>) (o2)).getValue());
            }
        });

        Map<K, V> result = new LinkedHashMap<>();
        for (Iterator<Map.Entry<K, V>> it = list.iterator(); it.hasNext(); ) {
            Map.Entry<K, V> entry = (Map.Entry<K, V>) it.next();
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }

    private boolean checkRoomOverLapping(CourseClass mainCourseClass) {
        if (mainCourseClass == null) return false;
        String roomId = mainCourseClass.getRoom().getId();

        for (CourseClass courseClass : mSlots.get(mainCourseClass.getDay() - 1).get(mainCourseClass.getTime() - 1)) {
            if (courseClass == null) continue;
            if (courseClass.equals(mainCourseClass)) continue;
            if (courseClass.getRoom().getId().equals(roomId)) return true;
        }

        return false;

    }

    private boolean checkCourseOverLapping(CourseClass mainCourseClass) {
        if (mainCourseClass == null) return false;
        int courseId = mainCourseClass.getCourse().getId();

        for (CourseClass courseClass : mSlots.get(mainCourseClass.getDay() - 1).get(mainCourseClass.getTime() - 1)) {
            if (courseClass == null) continue;
            if (courseClass.equals(mainCourseClass)) continue;
            if (courseClass.getCourse().getId() == courseId) return true;
        }

        return false;
    }


    private boolean checkProfessorOverLapping(CourseClass mainCourseClass) {
        if (mainCourseClass == null) return false;
        int professorId = mainCourseClass.getProfessor().getProfId();

        for (CourseClass courseClass : mSlots.get(mainCourseClass.getDay() - 1).get(mainCourseClass.getTime() - 1)) {
            if (courseClass == null) continue;
            if (courseClass.equals(mainCourseClass)) continue;
            if (courseClass.getProfessor().getProfId() == professorId) return true;
        }

        return false;
    }

    private boolean isProfessorProgramOk(CourseClass courseClass) {
        if (courseClass == null) return true;
        short[][] profTimeAvailability = courseClass.getProfessor().getProfFreeTime();
        int day = courseClass.getDay();
        int time = courseClass.getTime();
        return 1 == profTimeAvailability[day - 1][time - 1];
    }

    private boolean isClassProgramOk(CourseClass courseClass) {
        if (courseClass == null) return true;
        short[][] classFree = courseClass.getRoom().getFreeTimes();
        int day = courseClass.getDay();
        int time = courseClass.getTime();
        return 1 == classFree[day - 1][time - 1];
    }

    private int classCapacityRate(CourseClass courseClass) {

        // be shekle manfi

        boolean capacityGreaterThanTwenty = courseClass.getRoom().getIsCapacityGreaterThanTwenty();
        boolean needGreaterThanTwentySeats = courseClass.getCourse().isNeedGreaterThanTwentySeats();
        int score = 0;
        if (!capacityGreaterThanTwenty && !needGreaterThanTwentySeats) {
            score = 0;
        } else if (!capacityGreaterThanTwenty && needGreaterThanTwentySeats) {
            score = 2;
        } else if (capacityGreaterThanTwenty && !needGreaterThanTwentySeats) {
            score = 1;
        } else if (capacityGreaterThanTwenty && needGreaterThanTwentySeats) {
            score = 0;
        }

        return score;
    }

    private boolean isChartOk(CourseClass mainCourseClass) {
        if (mainCourseClass == null) return true;
        String studentGroup = mainCourseClass.getCourse().getStudentGroup();

        for (CourseClass courseClass : mSlots.get(mainCourseClass.getDay() - 1).get(mainCourseClass.getTime() - 1)) {
            if (courseClass == null) continue;
            if (courseClass.equals(mainCourseClass)) continue;
            if (courseClass.getCourse().getStudentGroup().equals(studentGroup)) return false;
        }

        return true;
    }

    private void removeCourseClass(CourseClass currentCourse) {
        for (int i = 0; i < WORKING_DAYS_NUM; i++) {
            for (int j = 0; j < DAY_SLOTS; j++) {
                for (int k = 0; k < mConfiguration.getNumberOfRooms(); k++) {
                    if (currentCourse.equals(mSlots.get(i).get(j).get(k))) {
                        mSlots.get(i).get(j).set(k, null);
                    }
                }
            }
        }
    }

    public List<List<List<CourseClass>>> getSlots() {
        return mSlots;
    }

    public List<CourseClass> getClasses() {
        return mClasses;
    }

    public int getMutationSize() {
        return mMutationSize;
    }

    public float getFitness() {
        return mFitness;
    }

    public int getCrossOverProbability() {
        return mCrossOverProbability;
    }

    public int getMutationProbability() {
        return mCrossOverProbability;
    }

    public float getNumberOfCrossoverPoints() {
        return mNumberOfCrossoverPoints;
    }
}