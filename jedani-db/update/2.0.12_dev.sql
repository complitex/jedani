DROP PROCEDURE createDomainTables;

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
            `domain_id`           BIGINT(20) NOT NULL COMMENT ''Идентификатор домена'',
            `entity_attribute_id` BIGINT(20) NOT NULL COMMENT ''Идентификатор типа атрибута'',
            `text`                VARCHAR(255) COMMENT ''Текст'',
            `number`              BIGINT(20) COMMENT ''Число'',
            `date`                DATETIME COMMENT ''Дата'',
            `start_date`          TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT ''Дата начала периода действия атрибута'',
            `end_date`            TIMESTAMP  NULL     DEFAULT NULL COMMENT ''Дата окончания периода действия атрибута'',
            `status`              INTEGER    NOT NULL DEFAULT 1 COMMENT ''Статус'',
            `user_id`             BIGINT(20) NULL COMMENT ''Идентифитактор пользователя'',
            PRIMARY KEY (`id`),
            KEY `key_domain_id` (`domain_id`),
            KEY `key_entity_attribute_id` (`entity_attribute_id`),
            KEY `key_text` (`text`),
            KEY `key_number` (`number`),
            KEY `key_date` (`date`),
            KEY `key_start_date` (`start_date`),
            KEY `key_end_date` (`end_date`),
            KEY `key_status` (`status`),
            CONSTRAINT `fk_', entityName, '_attribute__', entityName, '` FOREIGN KEY (`domain_id`) REFERENCES `', entityName, '` (`id`) ON DELETE CASCADE,
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
            CONSTRAINT `fk_', entityName, '_value__', entityName, '_attribute` FOREIGN KEY (`attribute_id`) REFERENCES `', entityName, '_attribute` (`id`)  ON DELETE CASCADE,
            CONSTRAINT `fk_', entityName, '_value__locale` FOREIGN KEY (`locale_id`) REFERENCES `locale` (`id`)
        ) ENGINE = InnoDB
          CHARSET = utf8
          COLLATE = utf8_unicode_ci COMMENT ''', entityDescription, ' - Значения атрибутов'';');

    PREPARE QUERY FROM @createValue; EXECUTE QUERY; DEALLOCATE PREPARE QUERY;
END //

DELIMITER ;


ALTER TABLE reward_attribute DROP FOREIGN KEY fk_reward_attribute__reward;

ALTER TABLE reward_attribute ADD CONSTRAINT fk_reward_attribute__reward
    FOREIGN KEY (domain_id) REFERENCES reward (id) ON DELETE CASCADE;


ALTER TABLE reward_value DROP FOREIGN KEY fk_reward_value__reward_attribute;

ALTER TABLE reward_value ADD CONSTRAINT fk_reward_value__reward_attribute
    FOREIGN KEY (attribute_id) REFERENCES reward_attribute (id) ON DELETE CASCADE;


CALL createDomainTables('worker_node', 'Структура');

CALL createEntity(48, 'worker_node', 'Структура', 'Структура');
CALL createEntityAttributeWithReference(48, 1, 11, 20, 'Сотрудник', 'Співробітник');
CALL createEntityAttributeWithReference(48, 2, 11, 20, 'Менеджер', 'Менеджер');
CALL createEntityAttribute(48, 3, 5, 'Лево', 'Лівий');
CALL createEntityAttribute(48, 4, 5, 'Право', 'Право');
CALL createEntityAttribute(48, 5, 5, 'Уровень', 'Рівень');
CALL createEntityAttributeWithReference(48, 6, 11, 42, 'Операционный месяц', 'Операційний місяць');


INSERT INTO `update` (`version`) VALUE ('2.0.12');