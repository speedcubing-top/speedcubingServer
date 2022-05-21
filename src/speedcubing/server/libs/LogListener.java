package speedcubing.server.libs;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.message.Message;
import speedcubing.server.speedcubingServer;

import java.util.Iterator;
import java.util.regex.Pattern;

public class LogListener {
//    public static boolean Listening = false;

    public void reloadFilter() {
        Logger coreLogger = (Logger) LogManager.getRootLogger();
        Filter filter = new Filter() {

            @Override
            public Result filter(LogEvent event) {
                String msg = event.getMessage().getFormattedMessage();
                for (Pattern p : speedcubingServer.blockedLog) {
                    if (p.matcher(msg).matches())
                        return Result.DENY;
                }
//                if (Listening) {
//                    String string = Console.ansiToColoredText(msg);
//                    StringBuilder unicode = new StringBuilder();
//                    for (int i = 0; i < string.length(); i++) {
//                        unicode.append("\\u").append(Integer.toHexString(string.charAt(i)));
//                    }
//                    new Thread(() -> {
//                        try {
//                            speedcubingServer.tcp.sendUnsafe(User.getUser(uuid).tcpPort, "t|" + speedcubingServer.TCP + "|" + unicode);
//                        } catch (Exception e) {
//                        }
//                    }).start();
//                }
                return null;
            }

            @Override
            public State getState() {
                return null;
            }

            @Override
            public void initialize() {

            }

            @Override
            public void start() {

            }

            @Override
            public void stop() {

            }

            @Override
            public boolean isStarted() {
                return false;
            }

            @Override
            public boolean isStopped() {
                return false;
            }


            @Override
            public Result getOnMatch() {
                return null;
            }

            @Override
            public Result filter(Logger logger, Level level, Marker marker, String
                    s, Object... objects) {
                return null;
            }

            @Override
            public Result filter(Logger logger, Level level, Marker marker, String
                    s, Object o) {
                return null;
            }

            @Override
            public Result filter(Logger logger, Level level, Marker marker, String
                    s, Object o, Object o1) {
                return null;
            }

            @Override
            public Result filter(Logger logger, Level level, Marker marker, String
                    s, Object o, Object o1, Object o2) {
                return null;
            }

            @Override
            public Result filter(Logger logger, Level level, Marker marker, String
                    s, Object o, Object o1, Object o2, Object o3) {
                return null;
            }

            @Override
            public Result filter(Logger logger, Level level, Marker marker, String
                    s, Object o, Object o1, Object o2, Object o3, Object o4) {
                return null;
            }

            @Override
            public Result filter(Logger logger, Level level, Marker marker, String
                    s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5) {
                return null;
            }

            @Override
            public Result filter(Logger logger, Level level, Marker marker, String
                    s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6) {
                return null;
            }

            @Override
            public Result filter(Logger logger, Level level, Marker marker, String
                    s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7) {
                return null;
            }

            @Override
            public Result filter(Logger logger, Level level, Marker marker, String
                    s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8) {
                return null;
            }

            @Override
            public Result filter(Logger logger, Level level, Marker marker, String
                    s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object
                                         o8, Object o9) {
                return null;
            }

            @Override
            public Result filter(Logger logger, Level level, Marker marker, Object
                    o, Throwable throwable) {
                return null;
            }

            @Override
            public Result filter(Logger logger, Level level, Marker marker, Message
                    message, Throwable throwable) {
                return null;
            }

            @Override
            public Result getOnMismatch() {
                return null;
            }
        };
        boolean alreadyLoaded = false;
        Iterator<Filter> iter = coreLogger.getFilters();
        while (iter.hasNext()) {
            if (filter.equals(iter.next())) {
                alreadyLoaded = true;
                break;
            }
        }
        if (!alreadyLoaded)
            coreLogger.addFilter(filter);
    }
}
