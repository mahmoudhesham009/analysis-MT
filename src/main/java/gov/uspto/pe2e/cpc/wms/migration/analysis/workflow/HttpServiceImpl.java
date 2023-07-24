package gov.uspto.pe2e.cpc.wms.migration.analysis.workflow;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class HttpServiceImpl {

	@Value("${spring.alfresco.rootURI}${spring.alfresco.apsURL}")
    String BASE_URL;

    @Value("${spring.alfresco.userName}")
    String USER;

    @Value("${spring.alfresco.password}")
    String PASSWORD;
    
    @Autowired
	private Environment env;

    public <T> T getReq(String path, Class<T> c) throws URISyntaxException {
        WebClient client = WebClient.create();

        return client.get()
                .uri(new URI(BASE_URL+path))
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION,getCredentials())
                .retrieve()
                .onStatus(
                        HttpStatus.NOT_FOUND::equals,
                        response ->{ throw new RuntimeException();})
                .bodyToMono(c)
                .block();
    }


    public <T> T postReq(String path, Object body, Class<T> c) throws URISyntaxException {
        WebClient client = WebClient.create();

        return client.post()
                .uri(new URI(BASE_URL+path))
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION,getCredentials())
                .bodyValue(body)
                .retrieve()
                .onStatus(
                        s-> !s.equals(HttpStatus.OK),
                        response ->{ throw new RuntimeException(response.toString());})
                .bodyToMono(c)
                .block();
    }


    public <T> T deleteReq(String path, Class<T> c) throws URISyntaxException {
        WebClient client = WebClient.create();
        return client.delete()
                .uri(new URI(BASE_URL+path))
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION,getCredentials())
                .retrieve()
                .onStatus(
                        HttpStatus.NOT_FOUND::equals,
                        response ->{ throw new RuntimeException();})
                .bodyToMono(c)
                .block();

    }

    private String getCredentials() {

        String creds = USER + ":"+ PASSWORD;
        creds = new String("Basic "+ Base64Utils
                .encodeToString((creds).getBytes(StandardCharsets.UTF_8)));
        return creds;
    }
}
