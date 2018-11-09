package test.ru;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class TestServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            //проверка пришел ли запрос в multipart формате
            if (isMultipartFormat(request)) {

                //разбор формата multipart и помещение информации из запроса в поля объекта
                //класса PostData
 //               PostData multidata = new PostData(request);

                //извлечение посланной информации
//                String fileDescription = multidata.getParameter("description ");
 //               FileData tempFile = multidata.getFileData("file_send");

//                if (tempFile != null) saveFile(tempFile);

                //ну и дальше какая-то генерация ответа кленту...
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }

    }

    //функция, проверяющая пришел ли запрос в формате multipart
    private boolean isMultipartFormat(HttpServletRequest req) throws javax.servlet.ServletException, java.io.IOException {
        String temptype = req.getContentType();
        if (temptype.indexOf("multipart/form-data") != -1) return true;
        else return false;
    }
/*
    //функция, сохраняющая пришедший файл на диск
    private void saveFile(FileData tempFile) throws IOException {
        File f = new File(“c:\\temp\\”+tempFile.getFileName());
        FileOutputStream fos = new FileOutputStream(f);
        fos.write(tempFile.getByteData());
        fos.close();
    }
*/
}