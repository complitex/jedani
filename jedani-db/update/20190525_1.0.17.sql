-- Add create domain tables procedure

DELIMITER //

CREATE PROCEDURE createDomainTables (IN entityName VARCHAR(64) CHARSET utf8, IN entityDescription VARCHAR(256) CHARSET utf8)
BEGIN
    SET @createDomain = CONCAT('
        CREATE TABLE `', entityName, '`
        (
            `id`               BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT ''Идентификатор'',
            `object_id`        BIGINT(20) NOT NULL COMMENT ''Идентификатор объекта'',
            `parent_id`        BIGINT(20) COMMENT ''Идентификатор родительского объекта'',
            `parent_entity_id` BIGINT(20) COMMENT ''Идентификатор сущности родительского объекта'',
            `start_date`       TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT ''Дата начала периода действия объекта'',
            `end_date`         TIMESTAMP  NULL     DEFAULT NULL COMMENT ''Дата окончания периода действия объекта'',
            `status`           INTEGER    NOT NULL DEFAULT 1 COMMENT ''Статус'',
            `permission_id`    BIGINT(20) NULL COMMENT ''Ключ прав доступа к объекту'',
            `user_id`          BIGINT(20) NULL COMMENT ''Идентифитактор пользователя'',
            PRIMARY KEY (`id`),
            UNIQUE KEY `unique_object_id__status` (`object_id`, `status`),
            KEY `key_object_id` (`object_id`),
            KEY `key_parent_id` (`parent_id`),
            KEY `key_parent_entity_id` (`parent_entity_id`),
            KEY `key_start_date` (`start_date`),
            KEY `key_end_date` (`end_date`),
            KEY `key_status` (`status`),
            KEY `key_permission_id` (`permission_id`),
            CONSTRAINT `fk_', entityName, '__entity` FOREIGN KEY (`parent_entity_id`) REFERENCES `entity` (`id`),
            CONSTRAINT `fk_', entityName, '__permission` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`permission_id`),
            CONSTRAINT `fk_', entityName, '__user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
        ) ENGINE = InnoDB
          CHARSET = utf8
          COLLATE = utf8_unicode_ci COMMENT ''', entityDescription, ''';');

    PREPARE QUERY FROM @createDomain; EXECUTE QUERY; DEALLOCATE PREPARE QUERY;

    SET @createAttribute = CONCAT('
        CREATE TABLE `', entityName, '_attribute`
        (
            `id`                  BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT ''Идентификатор'',
            `object_id`           BIGINT(20) NOT NULL COMMENT ''Идентификатор объекта'',
            `entity_attribute_id` BIGINT(20) NOT NULL COMMENT ''Идентификатор типа атрибута'',
            `text`                VARCHAR(255) COMMENT ''Текст'',
            `number`              BIGINT(20) COMMENT ''Число'',
            `date`                DATETIME COMMENT ''Дата'',
            `start_date`          TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT ''Дата начала периода действия атрибута'',
            `end_date`            TIMESTAMP  NULL     DEFAULT NULL COMMENT ''Дата окончания периода действия атрибута'',
            `status`              INTEGER    NOT NULL DEFAULT 1 COMMENT ''Статус'',
            `user_id`             BIGINT(20) NULL COMMENT ''Идентифитактор пользователя'',
            PRIMARY KEY (`id`),
            KEY `key_object_id` (`object_id`),
            KEY `key_entity_attribute_id` (`entity_attribute_id`),
            KEY `key_text` (`text`),
            KEY `key_number` (`number`),
            KEY `key_date` (`date`),
            KEY `key_start_date` (`start_date`),
            KEY `key_end_date` (`end_date`),
            KEY `key_status` (`status`),
            CONSTRAINT `fk_', entityName, '_attribute__', entityName, '` FOREIGN KEY (`object_id`) REFERENCES `', entityName, '` (`object_id`),
            CONSTRAINT `fk_', entityName, '_attribute__user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
        ) ENGINE = InnoDB
          CHARSET = utf8
          COLLATE = utf8_unicode_ci COMMENT ''', entityDescription, ' - Аттрибуты'';');

    PREPARE QUERY FROM @createAttribute; EXECUTE QUERY; DEALLOCATE PREPARE QUERY;

     SET @createValue = CONCAT('
        CREATE TABLE `', entityName, '_value`
        (
            `id`           BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT ''Идентификатор'',
            `attribute_id` BIGINT(20) NOT NULL COMMENT ''Идентификатор атрибута'',
            `locale_id`    BIGINT(20) COMMENT ''Идентификатор локали'',
            `text`         VARCHAR(1000) COMMENT ''Текстовое значение'',
            `number`       BIGINT(20) COMMENT ''Числовое значение'',
            PRIMARY KEY (`id`),
            UNIQUE KEY `unique_id__locale` (`attribute_id`, `locale_id`),
            KEY `key_attribute_id` (`attribute_id`),
            KEY `key_locale` (`locale_id`),
            KEY `key_value` (`text`(128)),
            CONSTRAINT `fk_', entityName, '_value__', entityName, '_attribute` FOREIGN KEY (`attribute_id`) REFERENCES `', entityName, '_attribute` (`id`),
            CONSTRAINT `fk_', entityName, '_value__locale` FOREIGN KEY (`locale_id`) REFERENCES `locale` (`id`)
        ) ENGINE = InnoDB
          CHARSET = utf8
          COLLATE = utf8_unicode_ci COMMENT ''', entityDescription, ' - Значения атрибутов'';');

    PREPARE QUERY FROM @createValue; EXECUTE QUERY; DEALLOCATE PREPARE QUERY;
END //

-- Add create entity procedures

CREATE PROCEDURE createEntity(IN id BIGINT, IN entityName VARCHAR(64) CHARSET utf8,
    IN entityDescriptionRU VARCHAR(128) CHARSET utf8, IN entityDescriptionUA VARCHAR(128) CHARSET utf8)
BEGIN
    SET @insertEntity = CONCAT('INSERT INTO `entity` (`id`, `name`) VALUE (',id, ', ''', entityName, ''');');

    PREPARE QUERY FROM @insertEntity; EXECUTE QUERY; DEALLOCATE PREPARE QUERY;

    SET @insertEntityValue = CONCAT('INSERT INTO `entity_value`(`entity_id`, `locale_id`, `text`) VALUES (', id,
        ', 1, ''', entityDescriptionRU, '''), (', id, ', 2, ''', entityDescriptionUA, ''');');

    PREPARE QUERY FROM @insertEntityValue; EXECUTE QUERY; DEALLOCATE PREPARE QUERY;
END //

CREATE PROCEDURE createEntityAttribute(IN entityId BIGINT, IN entityAttributeId BIGINT, IN valueTypeId BIGINT,
    IN entityDescriptionRU VARCHAR(128) CHARSET utf8, IN entityDescriptionUA VARCHAR(128) CHARSET utf8)
BEGIN
    SET @insertAttribute = CONCAT('INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (',
        entityId, ', ', entityAttributeId, ', ', valueTypeId, ');');

    PREPARE QUERY FROM @insertAttribute; EXECUTE QUERY; DEALLOCATE PREPARE QUERY;

    SET @insertEntityValue = CONCAT('INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (',
        entityId, ', ', entityAttributeId, ', 1, ''', entityDescriptionRU, '''), (',
        entityId, ', ', entityAttributeId, ', 2, ''', entityDescriptionUA, ''');');

    PREPARE QUERY FROM @insertEntityValue; EXECUTE QUERY; DEALLOCATE PREPARE QUERY;
END //

-- Add Sale Decision

DELIMITER ;

CALL createDomainTables('rule', 'Правило');
CALL createEntity(33, 'rule', 'Правило', 'Правило');

CALL createDomainTables('rule_condition', 'Условие правила');
CALL createEntity(34, 'rule_condition', 'Условие правила', 'Умова правила');
CALL createEntityAttribute(34, 1, 5, 'Индекс', 'Індекс');
CALL createEntityAttribute(34, 2, 5, 'Тип условия', 'Тип умови');

CALL createDomainTables('rule_action', 'Действие правила');
CALL createEntity(35, 'rule_action', 'Действие правила', 'Дії правила');
CALL createEntityAttribute(35, 1, 5, 'Индекс', 'Індекс');
CALL createEntityAttribute(35, 2, 5, 'Тип действия', 'Тип дії');

CALL createDomainTables('sale_decision', 'Условие продаж');
CALL createEntity(36, 'sale_decision', 'Условие продаж', 'Умова продажів');
CALL createEntityAttribute(36, 2, 5, 'Название', 'Назва');


-- Update

INSERT INTO `update` (`version`) VALUE ('20190525_1.0.17');