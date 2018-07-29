package moudles;

import java.util.Arrays;
import java.util.Objects;

public class Room {
    private String mId;
    private short[][] mFreeTimes;
    private boolean mIsCapacityGreaterThanTwenty;

    public Room(String id) {
        mId = id;
    }

    public Room() {
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public short[][] getFreeTimes() {
        return mFreeTimes;
    }

    public void setFreeTimes(short[][] freeTimes) {
        mFreeTimes = freeTimes;
    }

    public void setIsCapacityGreaterThanTwenty(boolean isCapacityGreaterThanTwenty) {
        mIsCapacityGreaterThanTwenty = isCapacityGreaterThanTwenty;
    }

    public boolean getIsCapacityGreaterThanTwenty() {
        return mIsCapacityGreaterThanTwenty;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Room)) return false;
        Room room = (Room) o;
        return mIsCapacityGreaterThanTwenty == room.mIsCapacityGreaterThanTwenty &&
                   Objects.equals(getId(), room.getId()) &&
                   Arrays.equals(getFreeTimes(), room.getFreeTimes());
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(getId(), mIsCapacityGreaterThanTwenty);
        result = 31 * result + Arrays.hashCode(getFreeTimes());
        return result;
    }

    @Override
    public String toString() {
        return "Room{" +
                   "mId='" + mId + '\'' +
                   ", mFreeTimes=" + Arrays.deepToString(mFreeTimes) +
                   ", mIsCapacityGreaterThanTwenty=" + mIsCapacityGreaterThanTwenty +
                   '}';
    }
}
