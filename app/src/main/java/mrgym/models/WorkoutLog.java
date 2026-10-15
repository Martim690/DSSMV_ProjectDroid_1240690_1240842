package mrgym.models;

public class WorkoutLog {
    private String _id;
    private String userId;
    private String date;
    private int durationMinutes;

    public WorkoutLog() {
    }

    public WorkoutLog(String userId, String date, int durationMinutes) {
        this.userId = userId;
        this.date = date;
        this.durationMinutes = durationMinutes;
    }

    public String getId() { return _id; }
    public void setId(String _id) { this._id = _id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }

    @Override
    public String toString() {
        return "WorkoutLog{date='" + date + "', duration=" + durationMinutes + "min}";
    }
}
