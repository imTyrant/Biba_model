package utils;

public class Hierarchy {

    final static int LOWEST_LEVEL = 4;

    public final static int LEVEL_SUPERVISOR = 0;
    public final static int LEVEL_CLASSIFED = 1;
    public final static int LEVEL_SECRET = 2;
    public final static int LEVEL_CONSTRAINED = 3;
    public final static int LEVEL_PUBLIC = 4;

    public static boolean isLegaleLevel(int level) {
        return (level > 0) && (level <= LOWEST_LEVEL);
    }
}
