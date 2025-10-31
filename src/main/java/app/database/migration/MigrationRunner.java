package app.database.migration;

import app.database.DatabaseManager;

public class MigrationRunner {
    private final DatabaseManager db;

    public MigrationRunner(DatabaseManager db) {
        this.db = db;
    }

    /**
     * Run the schema (and optionally seed) from resources/sql/.
     * Example usage:
     *   runner.runSchema(); // runs schema.sql
     *   runner.runSeed();   // runs seed.sql
     */
    public void runSchema() throws Exception {
        db.runSqlScriptResource("sql/schema.sql");
    }

    public void runSeed() throws Exception {
        db.runSqlScriptResource("sql/seed.sql");
    }
}
