package chronos;

import java.io.Serializable;
import java.util.List;

public record TK_TimerMem(String title, String time,
                          long timerTotalTime, long startTime, long stopTime,
                          List<LogElement> log)
implements Serializable {}
