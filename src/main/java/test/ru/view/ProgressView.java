package test.ru.view;

import com.google.gson.annotations.SerializedName;

import java.util.LinkedHashSet;
import java.util.Set;

public class ProgressView {

    @SerializedName("uploads")
    Set<FileUploadView> fileDataChannels = new LinkedHashSet<>();

    public ProgressView(Set<FileUploadView> fileDataChannels) {
        this.fileDataChannels = fileDataChannels;
    }

    public int size() {
        return fileDataChannels.size();
    }
}
