-- Fix domain constrain name

DELIMITER //

CREATE PROCEDURE fixConstraint(IN entityName VARCHAR(64))
BEGIN
    SET @drop = CONCAT('ALTER TABLE ', entityName, ' DROP FOREIGN KEY `ft_', entityName, '__entity`; ');

    PREPARE QUERY FROM @drop; EXECUTE QUERY; DEALLOCATE PREPARE QUERY;

    SET @alter = CONCAT('ALTER TABLE ', entityName, ' ADD CONSTRAINT `fk_', entityName, '__entity` FOREIGN KEY (`parent_entity_id`)',
        ' REFERENCES `entity` (`id`);');

    PREPARE QUERY FROM @alter; EXECUTE QUERY; DEALLOCATE PREPARE QUERY;
END //

DELIMITER ;

CALL fixConstraint('setting');
CALL fixConstraint('city');
CALL fixConstraint('last_name');
CALL fixConstraint('first_name');
CALL fixConstraint('middle_name');
CALL fixConstraint('worker');
CALL fixConstraint('mk_status');
CALL fixConstraint('position');
CALL fixConstraint('nomenclature');
CALL fixConstraint('storage');
CALL fixConstraint('product');
CALL fixConstraint('transaction');
CALL fixConstraint('promotion');
CALL fixConstraint('sale');
CALL fixConstraint('sale_item');
CALL fixConstraint('currency');
CALL fixConstraint('exchange_rate');
CALL fixConstraint('price');

DROP PROCEDURE fixConstraint;

-- Update

INSERT INTO `update` (`version`) VALUE ('20190524_1.0.16');