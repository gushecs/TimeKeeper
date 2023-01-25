package chronos;

import java.io.Serializable;
import java.util.List;

public record ChronosMem(boolean silence, boolean toggleDarkMode, List<Object> timeKeeperObjects) implements Serializable {}
