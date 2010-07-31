package reversi;

import java.lang.reflect.Constructor;

public class PlayerLoader {
    public static Player load(String name) {
        try {
            Class<?> cls = Class.forName(name);
            Constructor<?> ctor = cls.getConstructor((Class[]) null);
            Object obj = ctor.newInstance((Object[]) null);
            if (! (obj instanceof Player)) {
                System.err.println("Player " + name + " does not implement the Player interface");
                return null;
            } else {
                return (Player) obj;
            }
        } catch (Exception e) {
            System.err.println("loading of " + name + " failed: " + e.getMessage());
            return null;
        }
    }
}
