package dev.toszek.tiara.items;

import dev.toszek.tiara.items.infrastructure.config.SecurityApiKeyConstants;
import io.restassured.http.Header;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class BaseIntegrationTest {
    @LocalServerPort
    private Integer port;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    protected String getTestPath(String endpointPath) {
        String urlFormat = endpointPath.startsWith("/") ? "http://localhost:%d%s" : "http://localhost:%d/%s";
        return urlFormat.formatted(port, endpointPath);
    }

    protected Header getApiKeyHeader() {
        return new Header(SecurityApiKeyConstants.API_KEY_HEADER, "LetMeIn");
    }

    protected void clearH2Db() {
        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY FALSE");
        final List<String> tableNames = jdbcTemplate.queryForList("SELECT table_name FROM information_schema.tables WHERE table_schema = SCHEMA()", String.class);
        for (String tableName : tableNames) {
            jdbcTemplate.execute("TRUNCATE TABLE \"" + tableName + "\" RESTART IDENTITY");
        }
        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY TRUE");
    }
}
