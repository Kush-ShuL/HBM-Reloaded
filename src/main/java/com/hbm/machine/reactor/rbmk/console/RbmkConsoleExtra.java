package com.hbm.machine.reactor.rbmk.console;
import java.util.Map;
/** Real later column modules supply their own non-display OC fields (outgasser requiredFlux/input etc.). */
public interface RbmkConsoleExtra { void consoleComputerData(Map<String,Object> data); }
