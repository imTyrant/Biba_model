package utils;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

public class User implements Serializable{
    int level;
    String name;
    BibaFileHandler bibaFileHandler;

    public User(int level, String name) {
        this.level = level;
        this.name = name;
        this.bibaFileHandler = BibaFileHandler.getInstance();
    }

    public int getLevel() {
        return level;
    }

    public User changeLevel(int level) {
        this.level = level;
        return this;
    }

    public String getName() {
        return name;
    }

    public File accessFile(String path, int type) throws RuleConflictException {
        return bibaFileHandler.accessBibaFile(this.name, path, type);
    }

    public BibaFile createFile(String path) throws IOException {

        return bibaFileHandler.createNewFile(name, path);
    }
}
