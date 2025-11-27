package axo.dev.rpdepent.utilis;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.List;
import java.util.Map;

public final class configScrapper {

    private static final String target = "";

    private configScrapper() { /* utility class */ }

    public static void processRpdFiles(Path rootDir, String target, Consumer<String> lineHandler) throws IOException {
        if (rootDir == null) throw new IllegalArgumentException("rootDir must not be null");
        if (lineHandler == null) throw new IllegalArgumentException("lineHandler must not be null");

        try (Stream<Path> stream = Files.walk(rootDir)) {
            stream.filter(Files::isRegularFile)
                    .forEach(p -> {
                        String fileName = p.getFileName() != null ? p.getFileName().toString() : "";
                        try {
                            if (fileName.toLowerCase().endsWith(".zip")) {
                                // ZIP file on disk: open with ZipFile for random access
                                try (ZipFile zf = new ZipFile(p.toFile())) {
                                    processZipFile(zf, lineHandler);
                                }
                            }
                            else {
                                switch (fileName.toLowerCase()) {
                                    case "rpd.txt":
                                        try (BufferedReader br = Files.newBufferedReader(p, StandardCharsets.UTF_8)) {
                                            String line;
                                            while ((line = br.readLine()) != null) lineHandler.accept(line);
                                        }
                                        break;
                                    case "rpd.json":
                                        Map<String, String> depends = new HashMap<>();
                                        depends = configFileParsers.parseJSON(p);
                                        List<String> dependsKeys = new ArrayList<>(depends.keySet());
                                        for(int i = 0; i < dependsKeys.size(); i++) {
                                            lineHandler.accept(dependsKeys.get(i));
                                        }
                                        break;
                                
                                    default:
                                        break;
                                }
                            }
                        } catch (IOException e) {
                            throw new RuntimeException("Error processing path: " + p, e);
                        }
                    });
        }
    }

    // Process a ZipFile (on-disk zip)
    private static void processZipFile(ZipFile zf, Consumer<String> lineHandler) throws IOException {
        Enumeration<? extends ZipEntry> entries = zf.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            if (entry.isDirectory()) continue;
            String entryName = getSimpleName(entry.getName());
            if (target.equals(entryName)) {
                try (InputStream is = zf.getInputStream(entry);
                     BufferedReader br = new BufferedReader(new java.io.InputStreamReader(is, StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = br.readLine()) != null) lineHandler.accept(line);
                }
            } else if (entry.getName().toLowerCase().endsWith(".zip")) {
                // nested zip entry: read entry bytes and recurse via ZipInputStream
                try (InputStream nestedIs = zf.getInputStream(entry)) {
                    byte[] nestedBytes = toByteArray(nestedIs);
                    try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(nestedBytes))) {
                        processZipInputStream(zis, lineHandler);
                    }
                }
            }
        }
    }

    // Process a ZipInputStream (used for nested zips or zips read from streams)
    private static void processZipInputStream(ZipInputStream zis, Consumer<String> lineHandler) throws IOException {
        ZipEntry entry;
        while ((entry = zis.getNextEntry()) != null) {
            if (entry.isDirectory()) {
                zis.closeEntry();
                continue;
            }
            String entryName = getSimpleName(entry.getName());
            if (target.equals(entryName)) {
                try (BufferedReader br = new BufferedReader(new java.io.InputStreamReader(zis, StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = br.readLine()) != null) lineHandler.accept(line);
                }
                // ZipInputStream should not be closed here; we just finish this entry
            } else if (entry.getName().toLowerCase().endsWith(".zip")) {
                // Read nested zip entry bytes, then recurse
                byte[] nestedBytes = toByteArray(zis);
                try (ZipInputStream nestedZis = new ZipInputStream(new ByteArrayInputStream(nestedBytes))) {
                    processZipInputStream(nestedZis, lineHandler);
                }
            } else {
                // skip other entries (reading to exhaust the entry)
                // ensure stream position is at end of entry for nextEntry()
                byte[] buffer = new byte[8192];
                while (zis.read(buffer) > 0) { /* discard */ }
            }
            zis.closeEntry();
        }
    }

    // Helper: read all bytes from an InputStream (used for nested zip entries)
    private static byte[] toByteArray(InputStream is) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            byte[] buf = new byte[8192];
            int r;
            while ((r = is.read(buf)) != -1) baos.write(buf, 0, r);
            return baos.toByteArray();
        }
    }

    // Helper: return filename part of a path-like entry name (handles directories)
    private static String getSimpleName(String entryName) {
        if (entryName == null) return "";
        int slash = Math.max(entryName.lastIndexOf('/'), entryName.lastIndexOf('\\'));
        return slash >= 0 ? entryName.substring(slash + 1) : entryName;
    }

}