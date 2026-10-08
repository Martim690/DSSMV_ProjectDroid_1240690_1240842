package pt.isep.dssmv.mrgym.models;

public class User {
    private String _id;
    private String email;
    private String password;
    private String name;

    public User(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    public String getId() { return _id; }
    public String getEmail() { return email; }
    public String getName() { return name; }
}
