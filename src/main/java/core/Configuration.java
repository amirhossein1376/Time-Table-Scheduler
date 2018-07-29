package core;

import java.util.List;

import moudles.Course;
import moudles.Professor;
import moudles.Room;
import moudles.Schedule;

public class Configuration {

    public static final int POPULATION = 1000;
    public static final int REPLACE_BY_GENERATION = 500;
    public static final int BEST_POPULATION_TRACK_SIZE = 500;
    public static final int MAX_GENERATION = 300000 / POPULATION;
    public static final int CROSS_OVER_PROBABILITY = 85;
    public static final int CROSS_OVER_POINTS = 2;
    public static final int MUTATION_PROBABILITY = 65;
    public static final int MUTATION_SIZE = 2;
    public static final int HARD_LIMITATIONS = 5;
    public static final int SOFT_LIMITATIONS = 2;
    public static final float K_Ratio = 1000;

    private final List<Room> mRooms;
    private final List<Professor> mProfessors;
    private final List<Course> mCourses;

    private DataUtil mDataUtil;

    public Configuration(DataUtil dataUtil) {
        mDataUtil = dataUtil;

        mRooms = mDataUtil.getRooms();
        mDataUtil.setCapacityForRooms(mRooms);

        mProfessors = mDataUtil.getProfessors();
        mDataUtil.setProfessorsSkills(mProfessors);

        mCourses = mDataUtil.getCourses();
        mDataUtil.setSeatsNeedForCourses(mCourses);
        mDataUtil.setStudentGroupForCourses(mCourses);
    }

    public void saveSchedule(Schedule schedule) {
        mDataUtil.saveSchedule(this, schedule);
    }

    public int getNumberOfRooms() {
        return mRooms.size();
    }

    public List<Room> getRooms() {
        return mRooms;
    }

    public List<Course> getCourses() {
        return mCourses;
    }

    public List<Professor> getProfessors() {
        return mProfessors;
    }

    public void saveProfFile(Schedule schedule, int[] courses) {
        mDataUtil.saveProfFile(schedule, courses);
    }

    public DataUtil getDataUtil() {
        return mDataUtil;
    }

    public void setDataUtil(DataUtil dataUtil) {
        mDataUtil = dataUtil;
    }

    public void saveResults() {
        mDataUtil.saveResults();
    }
}
