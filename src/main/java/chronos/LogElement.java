package chronos;

import java.io.Serializable;
import java.util.Date;

public record LogElement(LogType logType, Date logTime, String logText) implements Serializable {
}
