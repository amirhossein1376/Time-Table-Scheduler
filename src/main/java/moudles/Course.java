package moudles;

import java.util.Objects;

public class Course {
    private int mId;
    private String mName;
    private boolean isNeedGreaterThanTwentySeats;
    private String mStudentGroup;

    public Course(int id, String name) {
        mId = id;
        mName = name;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public boolean isNeedGreaterThanTwentySeats() {
        return isNeedGreaterThanTwentySeats;
    }

    public void setNeedGreaterThanTwentySeats(boolean needGreaterThanTwentySeats) {
        isNeedGreaterThanTwentySeats = needGreaterThanTwentySeats;
    }

    public String getStudentGroup() {
        return mStudentGroup;
    }

    public void setStudentGroup(String studentGroup) {
        mStudentGroup = studentGroup;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Course)) return false;
        Course course = (Course) o;
        return getId() == course.getId() &&
                   isNeedGreaterThanTwentySeats() == course.isNeedGreaterThanTwentySeats() &&
                   Objects.equals(getName(), course.getName()) &&
                   Objects.equals(getStudentGroup(), course.getStudentGroup());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getId(), getName(), isNeedGreaterThanTwentySeats(), getStudentGroup());
    }

    @Override
    public String toString() {
        return "Course{" +
                   "mId=" + mId +
                   ", mName='" + mName + '\'' +
                   ", isNeedGreaterThanTwentySeats=" + isNeedGreaterThanTwentySeats +
                   ", mStudentGroup='" + mStudentGroup + '\'' +
                   '}';
    }
}
