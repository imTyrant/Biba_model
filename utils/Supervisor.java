package utils;

public class Supervisor extends User {

    public Supervisor(String name) {
        super(Hierarchy.LEVEL_SUPERVISOR, name);
        bibaFileHandler.createNewUser(Hierarchy.LEVEL_SUPERVISOR, name);
    }

    public User createUser(int level, String name) {
        User user =  new Governor(level, name);
        return user;
    }

    public void grantUserNewLevel(User user, int level) {
        if ((user != null) && Hierarchy.isLegaleLevel(level)) {
            user.level = level;
        }
    }

    public boolean revokeUser(User user) {
        if (user != null) {
            bibaFileHandler.revokeUser(user.name);
            return true;
        }
        return false;
    }
}
