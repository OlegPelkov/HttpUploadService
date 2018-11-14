package test.ru.view;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class FileUploadView {

    @SerializedName("id")
    private final String id;
    @SerializedName("size")
    private final int size;
    @SerializedName("uploaded")
    private final long uploaded;

    public FileUploadView(String id, int size, long uploaded) {
        this.id = id;
        this.size = size;
        this.uploaded = uploaded;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileUploadView that = (FileUploadView) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
