DELIMITER //

CREATE PROCEDURE updateNomenclatureType() BEGIN
    DECLARE done BOOLEAN DEFAULT FALSE;
    DECLARE domain_id BIGINT UNSIGNED;
    DECLARE cur CURSOR FOR SELECT n.id FROM nomenclature n
        LEFT JOIN nomenclature_attribute na_t ON na_t.domain_id = n.id AND na_t.entity_attribute_id = 4 AND na_t.status = 1
            WHERE na_t.id IS NULL OR na_t.number IS NULL;

    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done := TRUE;

    OPEN cur;

    _loop: LOOP
        FETCH cur INTO domain_id;
        IF done THEN
            LEAVE _loop;
        END IF;
        INSERT INTO nomenclature_attribute(domain_id, entity_attribute_id, number, user_id)
            VALUE (domain_id, 4, 2, 1);
    END LOOP _loop;

    CLOSE cur;
END //

DELIMITER ;

CALL updateNomenclatureType();

-- Update

INSERT INTO `update` (`version`) VALUE ('20190830_1.0.28');