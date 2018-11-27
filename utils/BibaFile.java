package utils;

import java.io.File;
import java.io.Serializable;

public class BibaFile implements Serializable {
    public final static int ACCESS_TYPE_READ = 0;
    public final static int ACCESS_TYPE_WRITE = 1;
    public final static int ACCESS_TYPE_READ_WRITE = 2;

    private int level;
    private String path;

    public BibaFile(String pathname) {
        this.path = pathname;
        this.level = Hierarchy.LEVEL_PUBLIC;
    }

    public BibaFile(String pathname, int level) {
        this.path = pathname;
        this.level = level;
    }

    public String getPath() {
        return this.path;
    }

    public int getLevel() {
        return this.level;
    }

    public BibaFile changeLevel(int level) {
        this.level = level;
        return this;
    }
}
