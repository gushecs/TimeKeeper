package chronos;

import java.io.Serializable;
import java.util.List;

public record TK_ChronometerMem(String title, String time,
                                long startTime, long stopTime,
                                List<LogElement> log)
implements Serializable {}
