package fhict.boards;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
        info = @Info(
                title = "Boards API",
                version = "latest",
                description = "The API for Boards, the project management and collaboration platform.",
                license = @License(
                        name = "MIT License",
                        url = "https://github.com/eduard-cc/boards-api/blob/main/LICENSE"
                )
        )
)
public class BoardsApplication {
    public static void main(String[] args) {
        SpringApplication.run(BoardsApplication.class, args);
    }
}
