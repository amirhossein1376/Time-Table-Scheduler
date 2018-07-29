package moudles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Professor {
    private int profId;
    private String profName;
    private short[] profSkills;
    private short[][] profFreeTime;
    private List<CourseClass> mCourseClasses = new ArrayList<CourseClass>();

    public int getProfId() {
        return profId;
    }

    public void setProfId(int profId) {
        this.profId = profId;
    }

    public String getProfName() {
        return profName;
    }

    public void setProfName(String profName) {
        this.profName = profName;
    }

    public short[] getProfSkills() {
        return profSkills;
    }

    public void setProfSkills(short[] profSkills) {
        this.profSkills = profSkills;
    }

    public short[][] getProfFreeTime() {
        return profFreeTime;
    }

    public void setProfFreeTime(short[][] profFreeTime) {
        this.profFreeTime = profFreeTime;
    }

    public List<CourseClass> getCourseClasses() {
        return mCourseClasses;
    }

    public void addCourseClass(CourseClass courseClasses) {
        mCourseClasses.add(courseClasses);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Professor)) return false;
        Professor professor = (Professor) o;
        return getProfId() == professor.getProfId() &&
                   Objects.equals(getProfName(), professor.getProfName()) &&
                   Arrays.equals(getProfSkills(), professor.getProfSkills()) &&
                   Arrays.equals(getProfFreeTime(), professor.getProfFreeTime()) &&
                   Objects.equals(getCourseClasses(), professor.getCourseClasses());
    }

    @Override
    public int hashCode() {

        int result = Objects.hash(getProfId(), getProfName(), getCourseClasses());
        result = 31 * result + Arrays.hashCode(getProfSkills());
        result = 31 * result + Arrays.hashCode(getProfFreeTime());
        return result;
    }

    @Override
    public String toString() {
        return "Professor{" +
                   "profId=" + profId +
                   ", profName='" + profName + '\'' +
                   ", profSkills=" + Arrays.toString(profSkills) +
                   ", profFreeTime=" + Arrays.deepToString(profFreeTime) +
                   ", mCourseClasses=" + mCourseClasses +
                   '}';
    }
}
