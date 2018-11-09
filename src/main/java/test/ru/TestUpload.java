package test.ru;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Vector;

public class TestUpload extends HttpServlet {

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // здесь хранится содержимое запроса
        byte[] data;
        // максимальный объем хранимой информации
        final int size = 1024 * 1024;

        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();
        ServletInputStream in = request.getInputStream();
        BufferedInputStream bf = new BufferedInputStream((InputStream) in, size);
        // переменная all_data - служит для временного
        // хранения содержимого запроса
        byte[] all_data = new byte[size];
        int b, j = 0;
        while ((b = bf.read()) != -1) {
            all_data[j] = (byte) b;
        // в j хранится количество прочитанных байт
            j++;
        }
        data = new byte[j];
        for (int i = 0; i < j; i++)
            data[i] = all_data[i];
        all_data = null;

        String boundary = extractBoundary(
                request.getHeader("Content-Type"));

        FileInfo[] file = extractFiles(data, boundary);

        for (int i = 0; i < file.length; i++)
            file[i].SaveFile(data, System.getProperty("user.dir") +
                    System.getProperty("file.separator"));

    }

    // получаем имя файла, если файла нет, то пишем NO_FILE
    private String getFilename(String header) {
        String filename = "";
        header.toLowerCase();
        int index;
        if ((index = header.indexOf("filename=")) != -1) {
            int up_index = header.indexOf((int) '"', index + 1 + 9);
            // ищем закрывающую кавычку после filename="....
            // 9 +1 это длина filename="
            filename = header.substring(index + 9 + 1, up_index);
            // в разных ОС по разному представляется,
            index = filename.lastIndexOf((int) '/');
            // символ "file.separator"
            up_index = filename.lastIndexOf((int) '\\');
            filename = filename.substring
                    (Math.max(index, up_index) + 1);
        } else
            filename = "NO_FILE";

        return filename;

    }

    // в этой процедуре происходит
    // корректировка индексов(отделяется заголовок) и
    // находится имя файла (если есть)
    private void extractData(FileInfo fis, String data) {
        char[] ch = {'\r', '\n', '\r', '\n'};
        // этими символами отделен заголовок от содержимого
        // в байтах это выглядит {13,10,13,10}

        String new_line = new String(ch);
        String header;

        //отделяем заголовок
        int index = data.indexOf(new_line, 2);
        if (index != -1) {
            header = data.substring(0, index);
            fis.filename = getFilename(header);
            // 4 символа - это длина new_line
            fis.start_index += index + 4;
        }

    }

    // делим исходный запрос(data) на части
    // и получаем информацию о каждой части.
    private FileInfo[] extractFiles(byte[] data, String boundary) {
        int i = 0, index = 0, prev_index = 0;
        //со строкой удобнее работать
        String data_str = new String(data);
        //для временного хранения частей сообщения
        Vector data_vec = new Vector();

        // считаем количество частей
        // в сообщении(ограниченных boundary)
        while ((index = data_str.indexOf(boundary, index)) != -1) {
        // первый шаг - подготовка к индексации
            if (i != 0) {
        // здесь мы выделяем сообщение,
                // ограниченное boundary
                // (все сообщение: заголовок + содержание)

                FileInfo f_info =
                        new FileInfo("NO_FILE", prev_index, index - 2);
                //так как содержимое -
                //отделено от boundary двумя символами{'\r','\n'}
                //или в байтах {10,13}

                extractData(f_info,
                        data_str.substring(prev_index, index));
                data_vec.addElement(f_info);
            }
            index += boundary.length();
            prev_index = index;
            i++;
        }
        // i-1 должно равняться data_vec.capacity();

        FileInfo[] Data = new FileInfo[i - 1];
        Enumeration enume = data_vec.elements();
        i = 0;
        while (enume.hasMoreElements()) {
            Data[i] = (FileInfo) enume.nextElement();
            i++;
        }

        return Data;

    }

    // извлекаем границу
    private String extractBoundary(String str) {
        int index = str.lastIndexOf("boundary=");
        // 9- число букв в "boundary="
        String boundary = str.substring(index + 9);
        boundary = "--" + boundary;
        // так как реальная граница на два символа '-' длиннее,
        // чем записано в заголовке Content-Type.
        return boundary;
    }
}