package me.nelonn.quillspace;

import me.nelonn.quillspace.config.ArticlesProperties;
import me.nelonn.quillspace.config.RsaKeyProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties({RsaKeyProperties.class, ArticlesProperties.class})
@SpringBootApplication
public class QuillspaceBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(QuillspaceBackendApplication.class, args);
    }

}
