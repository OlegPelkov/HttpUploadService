package test.ru;

public class TaskHandler implements Runnable {
    @Override
    public void run() {
        try {
            WriteFileTask task = TaskContainer.getInstance().take();
            if (task != null) {
                task.write();
            }
        } catch (Exception e){

        }
    }
}
