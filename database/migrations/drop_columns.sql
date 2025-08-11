
-- HOW spring.jpa.hibernate.ddl-auto=update WORKS:
-- ====================================================

-- 1. AUTOMATIC SCHEMA UPDATES (What it DOES):
--    ✅ Creates new tables when @Entity classes are added
--    ✅ Adds new columns when fields are added to @Entity
--    ✅ Modifies column types when @Column annotations change
--    ✅ Creates indexes, foreign keys, constraints from annotations

-- 2. WHAT IT DOES NOT DO:
--    ❌ Does NOT drop tables automatically
--    ❌ Does NOT drop columns automatically (safety feature)
--    ❌ Does NOT rename columns automatically
--    ❌ Does NOT drop indexes/constraints automatically

-- 3. TYPICAL WORKFLOW:
--    Step 1: Modify your @Entity class (add/remove fields)
--    Step 2: Restart Spring Boot application
--    Step 3: Hibernate compares Entity vs Database schema
--    Step 4: Hibernate generates and executes DDL statements

-- 4. EXAMPLE SCENARIOS:

-- Scenario A: Add new field to Entity
-- @Entity public class User {
--     @Column private String newField; // <- Add this
-- }
-- Result: Hibernate executes: ALTER TABLE users ADD COLUMN new_field VARCHAR(255);

-- Scenario B: Remove field from Entity
-- @Entity public class User {
--     // @Column private String oldField; <- Remove this
-- }
-- Result: Column remains in database (manual cleanup needed)

-- 5. MANUAL CLEANUP FOR REMOVED FIELDS:
-- When you remove fields from Entity classes, clean up manually:

-- Example: Clean up columns that were removed from Entity classes
-- ALTER TABLE users DROP COLUMN IF EXISTS old_field_name;
-- ALTER TABLE courses DROP COLUMN IF EXISTS deprecated_column;
-- ALTER TABLE enrollments DROP COLUMN IF EXISTS unused_field;

-- 6. OTHER DDL-AUTO OPTIONS:
-- create: Drop and recreate schema on startup (DANGEROUS for production)
-- create-drop: Create on startup, drop on shutdown (for testing)
-- validate: Only validate schema matches entities (no changes)
-- none: No automatic schema management

-- 7. BEST PRACTICES:
-- ✅ Use 'update' for development
-- ✅ Use 'validate' for production
-- ✅ Always backup before schema changes
-- ✅ Test schema changes in development first
-- ❌ Never use 'create' or 'create-drop' in production
