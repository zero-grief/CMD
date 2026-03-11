package net.cmd.merge;

/**
 * Structured result of a rebuild/build step.
 */
public class CMDBuildResult {
    public boolean success = false;
    public int scannedSources = 0;
    public int parsedItemDefinitions = 0;
    public int registeredCases = 0;
    public int parseErrors = 0;
    public int writtenItemFiles = 0;
    public String outputZipPath = "";
    public String message = "";
}
