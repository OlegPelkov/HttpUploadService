package testUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import test.ru.channel.FileDataChannel;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FileValidator {

    private static final Logger LOG = LoggerFactory.getLogger(FileValidator.class);

    private List<File> listFilesForFolder(String path) throws IOException {
        return Files.walk(Paths.get(path))
                .filter(Files::isRegularFile)
                .map(Path::toFile)
                .collect(Collectors.toList());
    }

    public boolean compareFiles(Map<String, Integer> fileMap, String pathToDir) throws IOException {
        List<File> uploadedFiles = listFilesForFolder(pathToDir);
        boolean uploaded = false;
        for (Map.Entry<String, Integer> entry : fileMap.entrySet()) {
            uploaded = false;
            for (File file : uploadedFiles) {
                if (file.getName().equals(entry.getKey())) {
                    if (file.length() == entry.getValue().longValue()) {
                        uploaded = true;
                        continue;
                    }
                }
            }
        }
        if (!uploaded) {
            LOG.error("Error upload file ");
            return uploaded;
        }
        return  uploaded;
    }
}
