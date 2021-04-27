CALL createEntityAttributeWithReference(41, 12, 11, 30, 'Локальная валюта', 'Локальна валюта');

DELIMITER //

CREATE PROCEDURE addPaymentCurrency() BEGIN
    DECLARE done BOOLEAN DEFAULT FALSE;
    DECLARE domain_id BIGINT;
    DECLARE currency_id BIGINT;

    DECLARE cur CURSOR FOR SELECT p.object_id, coa_c.number FROM payment p
        LEFT JOIN payment_attribute pa_s ON pa_s.domain_id = p.id AND pa_s.entity_attribute_id = 9 AND pa_s.status = 1
        LEFT JOIN sale s ON s.object_id = pa_s.number AND s.status = 1
        LEFT JOIN sale_attribute sa_w ON sa_w.domain_id = s.id AND sa_w.entity_attribute_id = 1 AND sa_w.status = 1
        LEFT JOIN worker w ON w.object_id = sa_w.number AND w.status = 1
        LEFT JOIN worker_attribute wa_c ON wa_c.domain_id = w.id AND wa_c.entity_attribute_id = 7 AND wa_c.status = 1
        LEFT JOIN city c ON c.object_id = wa_c.number AND c.status = 1
        LEFT JOIN region r ON r.object_id = c.parent_id AND r.status = 1
        LEFT JOIN country co ON co.object_id = r.parent_id AND co.status = 1
        LEFT JOIN country_attribute coa_c ON coa_c.domain_id = co.id AND coa_c.entity_attribute_id = 3 AND coa_c.status = 1;

    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done := TRUE;

    OPEN cur;

    _loop: LOOP
        FETCH cur INTO domain_id, currency_id;

        IF done THEN
            LEAVE _loop;
        END IF;

        INSERT INTO payment_attribute(domain_id, entity_attribute_id, number) VALUE (domain_id, 12, currency_id);
    END LOOP _loop;

    CLOSE cur;
END //

DELIMITER ;

CALL addPaymentCurrency();

DROP PROCEDURE addPaymentCurrency;


INSERT INTO `update` (`version`) VALUE ('2.0.4');
