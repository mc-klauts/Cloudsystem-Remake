package io.dexiron.cloud.wrapper.manager;

import lombok.Getter;

import java.io.File;
import java.util.ArrayList;

import org.apache.commons.io.FileDeleteStrategy;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class ServerManager {

    @Getter
    private File staticDirectory = new File("server/static/");

    @Getter
    private File templatesDirectory = new File("server/templates/");

    @Getter
    private File tempDirectory = new File("server/temp/");

    @Getter
    private File globalDirectory = new File("server/templates/global/");

    @Getter
    private File deployDirectory = new File("server/deploy/");

    private ArrayList<Integer> ports = new ArrayList<>();

    public ServerManager() {
        if (!staticDirectory.exists()) {
            staticDirectory.mkdirs();
        }

        if (!deployDirectory.exists()) {
            deployDirectory.mkdirs();
        }

        if (!globalDirectory.exists()) {
            globalDirectory.mkdirs();
        }

        if (!tempDirectory.exists()) {
            tempDirectory.mkdirs();
        }
    }


    public void startServer(String name, String group, int ram, boolean isStatic) {
        try {

            File templates = new File((isStatic ? getStaticDirectory() : getTemplatesDirectory()), group);
            if (!templates.exists()) {
                templates.mkdirs();
            }

            File temporary = isStatic ? templates : new File(tempDirectory, name);
            if (!isStatic) {
                if (temporary.exists()) {
                    deleteDirectory(temporary);
                }
                FileUtils.copyDirectory(templates, temporary);
                FileUtils.copyDirectory(globalDirectory, temporary);
            } else {
                copyDirectoryCarefully(getGlobalDirectory(), temporary, 1482773874000L, 1);
            }

            File spigotJar = new File(temporary, "spigot.jar");
            if (!spigotJar.exists()) {
                System.out.println("Server [name=" + name + "] konnte nicht gestartet werden, weil die spigot.jar nicht vorhanden ist");
                return;
            }

            Integer port = getFreePort(41000);

            if (port == null) {
                System.out.println("Kein freier Port verfügbar");
                return;
            }

            ports.add(port);

            ProcessBuilder pb = new ProcessBuilder(
                    "/bin/sh", "-c",
                    "screen -mdS " + name
                            + " java -server "
                            + " -Xmx" + ram + "M"
                            + " -XX:+UseG1GC -XX:+ParallelRefProcEnabled -XX:MaxGCPauseMillis=200 -XX:+UnlockExperimentalVMOptions -XX:+DisableExplicitGC -XX:+AlwaysPreTouch -XX:G1NewSizePercent=30 -XX:G1MaxNewSizePercent=40 -XX:G1HeapRegionSize=8M -XX:G1ReservePercent=20 -XX:G1HeapWastePercent=5 -XX:G1MixedGCCountTarget=4 -XX:InitiatingHeapOccupancyPercent=15 -XX:G1MixedGCLiveThresholdPercent=90 -XX:G1RSetUpdatingPauseTimePercent=5 -XX:SurvivorRatio=32 -XX:+PerfDisableSharedMem -XX:MaxTenuringThreshold=1 "
                            + " -Dcloud-servername=" + name
                            + " -jar spigot.jar -o false -h 0.0.0.0 -p " + port
            ).directory(temporary);

            pb.start();

            System.out.println("Gameserver " + name + " was started!");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    private void deleteDirectory(File directory) {
        if (directory.exists()) {
            FileDeleteStrategy.FORCE.deleteQuietly(directory);
        }
    }

    private void copyDirectoryCarefully(File from, File to, long value, int layer) throws IOException {
        if (layer > 25) {
            return;
        }
        for (File file : from.listFiles()) {
            File toFile = new File(to, file.getName());
            if (file.isDirectory()) {
                copyDirectoryCarefully(file, toFile, value, layer + 1);
            } else {
                if (toFile.exists() && toFile.lastModified() != value) {
                    continue;
                }

                FileUtils.copyFileToDirectory(file, to);
                toFile.setLastModified(value);
            }
        }
    }

    private Integer getFreePort(int offset) {
        for (int p = offset; p <= offset + 1000; p++) {
            if (!ports.contains(p)) {
                return p;
            }
        }
        return null;
    }

}
