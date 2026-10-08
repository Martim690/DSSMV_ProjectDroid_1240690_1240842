package pt.isep.dssmv.mrgym.models;

public class WorkoutLog {
    private String _id;
    private String userId;
    private String date;
    private int durationMinutes;

    public WorkoutLog(String userId, String date, int durationMinutes) {
        this.userId = userId;
        this.date = date;
        this.durationMinutes = durationMinutes;
    }

    public String getDate() { return date; }
    public int getDurationMinutes() { return durationMinutes; }
}
