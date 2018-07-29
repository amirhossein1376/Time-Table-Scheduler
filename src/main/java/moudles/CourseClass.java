package moudles;

import java.util.Objects;

public class CourseClass {

    public static final int TIME_1 = 1; //8-10
    public static final int TIME_2 = 2; //10-12
    public static final int TIME_3 = 3; //13-15
    public static final int TIME_4 = 4; //15-17
    public static final int TIME_5 = 5; //17-19
    public static final int[] TIMES = new int[]{TIME_1, TIME_2, TIME_3, TIME_4 , TIME_5};


    public static final int DAY_SATURDAY = 1;
    public static final int DAY_SUNDAY = 2;
    public static final int DAY_MONDAY = 3;
    public static final int DAY_TUESDAY = 4;
    public static final int DAY_WEDNESDAY = 5;
    public static final int DAY_THURSDAY = 6;
    public static final int[] DAYS = new int[]{DAY_SATURDAY, DAY_SUNDAY, DAY_MONDAY, DAY_TUESDAY, DAY_WEDNESDAY , DAY_THURSDAY};

    private Professor mProfessor;
    private Course mCourse;
    private Room mRoom;
    private int mDuration = 2;
    private int day;
    private int time;

    public CourseClass() {
    }

    public CourseClass(Professor professor, Course course, int numberOfSeats, boolean requiresLab, int duration) {
        mProfessor = professor;
        mCourse = course;
        mDuration = duration;

        mProfessor.addCourseClass(this);
    }

    public CourseClass(Professor professor, Course course) {
        mProfessor = professor;
        mCourse = course;
    }

    public Professor getProfessor() {
        return mProfessor;
    }

    public void setProfessor(Professor professor) {
        mProfessor = professor;
    }

    public Course getCourse() {
        return mCourse;
    }

    public void setCourse(Course course) {
        mCourse = course;
    }

    public int getDuration() {
        return mDuration;
    }

    public void setDuration(int duration) {
        mDuration = duration;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public Room getRoom() {
        return mRoom;
    }

    public void setRoom(Room room) {
        mRoom = room;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CourseClass)) return false;
        CourseClass that = (CourseClass) o;
        return getDuration() == that.getDuration() &&
                   getDay() == that.getDay() &&
                   getTime() == that.getTime() &&
                   Objects.equals(getProfessor(), that.getProfessor()) &&
                   Objects.equals(getCourse(), that.getCourse()) &&
                   Objects.equals(getRoom(), that.getRoom());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getProfessor(), getCourse(), getRoom(), getDuration(), getDay(), getTime());
    }

    @Override
    public String toString() {
        return "CourseClass{" +
                   "mProfessor=" + mProfessor +
                   ", mCourse=" + mCourse +
                   ", mRoom=" + mRoom +
                   ", mDuration=" + mDuration +
                   ", day=" + day +
                   ", time=" + time +
                   '}';
    }
}
