package io.dexiron.cloud.lib.config.zip;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import org.apache.commons.io.FileUtils;

public class ZipFileHandler {
    public  void unZip(File zippedPath, String destinationPath) throws Exception {
        File destDir = new File(destinationPath);
        if (!destDir.exists()) {
            destDir.mkdir();
        }

        if (destDir.isDirectory()) {
            FileUtils.deleteDirectory(destDir);
        } else {
            FileUtils.deleteQuietly(destDir);
        }

        byte[] buffer = new byte[0x1FFF];
        ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zippedPath));
        ZipEntry zipEntry = zipInputStream.getNextEntry();
        while (zipEntry != null) {
            File newFile = new File(destinationPath + "/" + zipEntry.getName());
            if (zipEntry.isDirectory()) {
                newFile.mkdirs();
            } else {
                newFile.getParentFile().mkdirs();
                newFile.createNewFile();

                try (OutputStream outputStream = Files.newOutputStream(newFile.toPath())) {
                    int length;
                    while ((length = zipInputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, length);
                    }
                }
            }

            zipInputStream.closeEntry();
            zipEntry = zipInputStream.getNextEntry();
        }

        zipInputStream.closeEntry();
        zipInputStream.close();
    }


    private  void unZip(byte[] zippedBytes, File destDir) throws Exception {
        String destinationPath = destDir.toString();
        if (!destDir.exists()) {
            destDir.mkdir();
        }

        byte[] buffer = new byte[0x1FFF];
        try (ZipInputStream zipInputStream = new ZipInputStream(
                new ByteArrayInputStream(zippedBytes), StandardCharsets.UTF_8)) {
            ZipEntry zipEntry;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                File newFile = new File(destinationPath + "/" + zipEntry.getName());

                if (newFile.exists()) {
                    Files.deleteIfExists(newFile.toPath());
                }

                if (zipEntry.isDirectory()) {
                    Files.createDirectories(newFile.toPath());
                } else {
                    File parent = newFile.getParentFile();
                    if (!Files.exists(parent.toPath())) {
                        Files.createDirectories(parent.toPath());
                    }

                    Files.createFile(newFile.toPath());

                    try (OutputStream outputStream = Files.newOutputStream(newFile.toPath())) {
                        int length;
                        while ((length = zipInputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, length);
                        }

                        outputStream.flush();
                    }
                }

                zipInputStream.closeEntry();
            }

            zipInputStream.closeEntry();
        }
    }

    private  void toZip(byte[] zip, Path to) {
        try {
            Files.write(to, zip);
        } catch (final IOException ex) {
            ex.printStackTrace();
        }
    }

    public  void unZip(byte[] zippedBytes, String destinationPath) throws Exception {
        File destDir = new File(destinationPath);
        unZip(zippedBytes, destDir);
    }

    public  void unZip(byte[] zippedBytes, Path destinationPath) throws Exception {
        unZip(zippedBytes, destinationPath.toFile());
    }

    public  void toZip(byte[] zip, File to) {
        toZip(zip, to.toPath());
    }

    public  void toZip(byte[] zip, String to) {
        toZip(zip, Paths.get(to));
    }

    private  byte[] zipDirectoryToBytes(File file) {
        try {
            if (!file.exists()) {
                file.mkdirs();
            }

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ZipOutputStream zipOut = new ZipOutputStream(byteArrayOutputStream,
                    StandardCharsets.UTF_8);

            Files.walkFileTree(
                    file.toPath(),
                    EnumSet.noneOf(FileVisitOption.class),
                    Integer.MAX_VALUE,
                    new SimpleFileVisitor<Path>() {
                        @Override
                        public FileVisitResult visitFile(Path path, BasicFileAttributes attrs)
                                throws IOException {
                            try {
                                zipOut.putNextEntry(
                                        new ZipEntry(file.toPath().relativize(path).toString()));
                                Files.copy(path, zipOut);
                                zipOut.closeEntry();
                            } catch (final Throwable throwable) {
                                zipOut.closeEntry();
                            }

                            return FileVisitResult.CONTINUE;
                        }
                    }
            );

            zipOut.flush();
            zipOut.finish();
            zipOut.close();
            byte[] bytes = byteArrayOutputStream.toByteArray();
            byteArrayOutputStream.close();
            return bytes;
        } catch (final IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public  byte[] zipDirectoryToBytes(Path path) {
        return zipDirectoryToBytes(path.toFile());
    }

    public  byte[] zipDirectoryToBytes(String path) {
        return zipDirectoryToBytes(new File(path));
    }

    public  byte[] zipToBytes(Path path) {
        File file = path.toFile();
        try {
            if (!file.exists()) {
                return new byte[1024];
            }

            FileInputStream fileInputStream = new FileInputStream(file);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024];
            int read;
            while ((read = fileInputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, read);
            }

            return byteArrayOutputStream.toByteArray();
        } catch (final IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }


    public static Path zip(Path zipPath, Path... directories) throws IOException {
        if (directories == null) {
            return null;
        }

        if (!Files.exists(zipPath)) {
            Files.createFile(zipPath);
        }

        try (OutputStream outputStream = Files.newOutputStream(zipPath); ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream,
                StandardCharsets.UTF_8)) {
            for (Path dir : directories) {
                if (Files.exists(dir)) {
                    convert0(zipOutputStream, dir);
                }
            }
        }
        return zipPath;
    }

    private static void convert0(ZipOutputStream zipOutputStream, Path directory) throws IOException {
        Files.walkFileTree(directory, EnumSet.noneOf(FileVisitOption.class), Integer.MAX_VALUE, new SimpleFileVisitor<Path>() {
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                try {
                    zipOutputStream.putNextEntry(new ZipEntry(directory.relativize(file).toString()));
                    Files.copy(file, zipOutputStream);
                    zipOutputStream.closeEntry();
                } catch (Exception ex) {
                    zipOutputStream.closeEntry();
                }
                return FileVisitResult.CONTINUE;
            }
        });
    }
}
