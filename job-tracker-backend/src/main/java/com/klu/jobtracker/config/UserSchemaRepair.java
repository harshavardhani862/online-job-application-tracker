package com.klu.jobtracker.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

@Component
public class UserSchemaRepair {
    private static final Logger log = LoggerFactory.getLogger(UserSchemaRepair.class);
    private final DataSource dataSource;

    public UserSchemaRepair(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void repair() {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("ALTER TABLE users ADD COLUMN IF NOT EXISTS phone varchar(255)");
            statement.execute("ALTER TABLE users ADD COLUMN IF NOT EXISTS created_at timestamp");
            statement.execute("ALTER TABLE users ADD COLUMN IF NOT EXISTS subscription_status varchar(255)");
            statement.execute("UPDATE users SET phone = '' WHERE phone IS NULL");
            statement.execute("UPDATE users SET created_at = NOW() WHERE created_at IS NULL");
            statement.execute("UPDATE users SET subscription_status = 'FREE' WHERE subscription_status IS NULL");
        } catch (Exception e) {
            log.warn("Could not repair users table schema: {}", e.getMessage());
        }
    }
}
