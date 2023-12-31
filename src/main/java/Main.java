import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHeaders;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Main {
    public static ObjectMapper mapper = new ObjectMapper();
    public static void main(String[] args) throws IOException {
        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(5000)    // максимальное время ожидание подключения к серверу
                        .setSocketTimeout(30000)    // максимальное время ожидания получения данных
                        .setRedirectsEnabled(false) // возможность следовать редиректу в ответе
                        .build())
                .build();

        HttpGet request = new HttpGet("https://api.nasa.gov/planetary/apod?api_key=1MYawxfJ8OniG7GWaysbVSa7QKqziX1fAMYEPlIQ");
        request.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());

        CloseableHttpResponse response = httpClient.execute(request);

        Nasa nasa = mapper.readValue(response.getEntity().getContent(), new TypeReference<Nasa>() {});
        String url = nasa.getUrl();

        HttpGet nasaRequest = new HttpGet(url);
        CloseableHttpResponse nasaResponse = httpClient.execute(nasaRequest);
        byte[] content = nasaResponse.getEntity().getContent().readAllBytes();

        String[] urlList = url.split("/");
        String name = urlList[urlList.length - 1];
        String[] nameList = name.split("\\?");
        String fileName = nameList[0];

        File file = new File(fileName);
        try {
            if (file.createNewFile()) {
                try (FileOutputStream fos = new FileOutputStream(fileName)) {
                    fos.write(content, 0, content.length);
                }
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
