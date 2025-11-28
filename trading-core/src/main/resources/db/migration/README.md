# Flyway Migration Rules

## Naming Convention

Flyway migrations follow this pattern:
```
V{version}__{description}.sql
```

Examples:
- `V1__create_tables.sql`
- `V2__seed_initial_data.sql`
- `V3__add_user_email_column.sql`

## Key Rules

### 1. **Versioned Migrations (V)**
- Start with `V` followed by version number
- Use double underscore `__` before description
- Version must be unique and sequential
- **NEVER modify or delete existing migrations**
- Once applied, migrations are immutable

### 2. **Version Numbering**
- Use integers: `V1`, `V2`, `V3`
- Or semantic: `V1.0`, `V1.1`, `V2.0`
- Or timestamps: `V20241128001`, `V20241128002`

### 3. **Repeatable Migrations (R)**
- Start with `R` instead of `V`
- Run every time checksum changes
- Use for views, procedures, functions
- Example: `R__create_user_view.sql`

### 4. **Snapshot Rules**

#### Creating Snapshots
```bash
# Generate current schema snapshot
./gradlew flywaySnapshot
```

#### When to Create Snapshots
- Before major refactoring
- After significant schema changes
- Before production deployments
- For disaster recovery planning

#### Snapshot Best Practices
- Store snapshots in version control
- Tag with release version
- Document what changed
- Keep snapshots separate from migrations

### 5. **Migration Best Practices**

#### DO:
- Write idempotent migrations when possible
- Test migrations on dev/staging first
- Include rollback scripts in comments
- Keep migrations small and focused
- Use transactions (implicit in Flyway)

#### DON'T:
- Modify existing migration files
- Delete applied migrations
- Use database-specific syntax (prefer ANSI SQL)
- Include application logic in migrations
- Commit broken migrations

### 6. **Handling Failures**

If migration fails:
```sql
-- Check migration status
SELECT * FROM flyway_schema_history;

-- Repair failed migration (use with caution)
./gradlew flywayRepair

-- Manually fix and re-run
./gradlew flywayMigrate
```

### 7. **Baseline Existing Database**
```yaml
spring:
  flyway:
    baseline-on-migrate: true  # Creates baseline for existing DB
    baseline-version: 0        # Starting version
```

### 8. **Common Patterns**

#### Adding Column
```sql
-- V3__add_user_email.sql
ALTER TABLE users ADD COLUMN email VARCHAR(100);
CREATE INDEX idx_user_email ON users(email);
```

#### Modifying Column (Safe)
```sql
-- V4__increase_username_length.sql
ALTER TABLE users ALTER COLUMN username VARCHAR(200);
```

#### Data Migration
```sql
-- V5__migrate_user_data.sql
UPDATE users SET status = 'ACTIVE' WHERE status IS NULL;
```

### 9. **Environment-Specific Migrations**

Use Flyway callbacks for environment-specific logic:
```
db/migration/
  V1__create_tables.sql
  V2__seed_initial_data.sql
db/callbacks/
  afterMigrate.sql
  beforeMigrate.sql
```

### 10. **Validation**

Flyway validates:
- Migration checksums haven't changed
- Migrations applied in correct order
- No missing migrations in sequence

Disable validation (not recommended):
```yaml
spring:
  flyway:
    validate-on-migrate: false
```

## Quick Reference

```bash
# Apply migrations
./gradlew flywayMigrate

# Check migration status
./gradlew flywayInfo

# Validate migrations
./gradlew flywayValidate

# Repair metadata table
./gradlew flywayRepair

# Clean database (DANGEROUS - dev only)
./gradlew flywayClean
```

## Example Workflow

1. Create new migration file: `V3__add_feature.sql`
2. Test locally: `./gradlew flywayMigrate`
3. Verify: `./gradlew flywayInfo`
4. Commit to version control
5. Deploy to staging
6. Deploy to production

## Troubleshooting

**Problem**: "Checksum mismatch"
**Solution**: Someone modified an applied migration. Use `flywayRepair` or revert changes.

**Problem**: "Migration failed"
**Solution**: Check logs, fix SQL, use `flywayRepair`, then `flywayMigrate`.

**Problem**: "Out of order migration"
**Solution**: Set `out-of-order: true` in config (not recommended for production).
