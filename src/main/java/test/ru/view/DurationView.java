package test.ru.view;

import java.util.LinkedHashSet;
import java.util.Set;

public class DurationView {

    private Set<String> fileDataChannels = new LinkedHashSet<>();
    private StringBuilder sb = new StringBuilder();

    public DurationView(Set<String> fileDataChannels) {
        this.fileDataChannels = fileDataChannels;
    }

    public String getView(){
        for (String s : fileDataChannels){
            sb.append(s);
            sb.append("\n");
        }
        return sb.toString();
    }
}
