DELIMITER //

CREATE PROCEDURE createEntityAttributeWithReference(IN entityId BIGINT, IN entityAttributeId BIGINT, IN valueTypeId BIGINT, IN referenceId BIGINT,
    IN entityDescriptionRU VARCHAR(128) CHARSET utf8, IN entityDescriptionUA VARCHAR(128) CHARSET utf8)
BEGIN
    SET @insertAttribute = CONCAT('INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`, `reference_id`) VALUES (',
        entityId, ', ', entityAttributeId, ', ', valueTypeId,  ', ', referenceId, ');');

    PREPARE QUERY FROM @insertAttribute; EXECUTE QUERY; DEALLOCATE PREPARE QUERY;

    SET @insertEntityValue = CONCAT('INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (',
        entityId, ', ', entityAttributeId, ', 1, ''', entityDescriptionRU, '''), (',
        entityId, ', ', entityAttributeId, ', 2, ''', entityDescriptionUA, ''');');

    PREPARE QUERY FROM @insertEntityValue; EXECUTE QUERY; DEALLOCATE PREPARE QUERY;
END //

DELIMITER ;

CALL createDomainTables('card', 'Карта');

CALL createEntity(37, 'card', 'Карта', 'Карта');
CALL createEntityAttribute(37, 1, 2, 'Номер карты', 'Номер картки');
CALL createEntityAttribute(37, 2, 6, 'Дата создания', 'Дата створення');
CALL createEntityAttributeWithReference(37, 3, 11, 20, 'Сотрудник', 'Сотрудник');
CALL createEntityAttribute(37, 4, 5, 'Индекс', 'Індекс');

-- Update

INSERT INTO `update` (`version`) VALUE ('20190604_1.0.18');