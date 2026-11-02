package mrgym.models;

public class JournalEntry {

    private String _id;
    private String id;
    private String photoPath;
    private long timestamp;
    private double weight;
    private double height;
    private String userEmail;

    public JournalEntry() {
    }

    public JournalEntry(String id, String photoPath, long timestamp, double weight, double height) {
        this.id = id;
        this.photoPath = photoPath;
        this.timestamp = timestamp;
        this.weight = weight;
        this.height = height;
    }

    public JournalEntry(String id, String photoPath, long timestamp, double weight, double height, String userEmail) {
        this.id = id;
        this.photoPath = photoPath;
        this.timestamp = timestamp;
        this.weight = weight;
        this.height = height;
        this.userEmail = userEmail;
    }

    public String getId() {
        return _id != null ? _id : id;
    }

    public void setId(String id) {
        this.id = id;
        this._id = id;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    @Override
    public String toString() {
        return "JournalEntry{" +
                "id='" + getId() + '\'' +
                ", photoPath='" + photoPath + '\'' +
                ", timestamp=" + timestamp +
                ", weight=" + weight +
                ", height=" + height +
                ", userEmail='" + userEmail + '\'' +
                '}';
    }
}
