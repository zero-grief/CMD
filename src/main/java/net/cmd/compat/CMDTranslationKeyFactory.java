package net.cmd.compat;

public class CMDTranslationKeyFactory {
    public static String fromModelPath(String modelPath) {
        String[] split = modelPath.split(":");
        if (split.length != 2) return "cmd.model.unknown";
        String namespace = split[0];
        String path = split[1];
        if (path.startsWith("item/")) path = path.substring("item/".length());
        path = path.replace("/", ".");
        return "cmd.model." + namespace + "." + path;
    }
}
