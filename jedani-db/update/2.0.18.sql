CALL createEntityAttributeWithReference(43, 6, 11, 50, 'Параметр', 'Параметр');

DELIMITER //

CREATE FUNCTION selectParameter(parameterId BIGINT) RETURNS BIGINT READS SQL DATA
BEGIN
    DECLARE objectId BIGINT;

    SELECT p.object_id INTO objectId FROM parameter p
                                LEFT JOIN parameter_attribute pa ON p.id = pa.domain_id AND pa.entity_attribute_id = 1 AND pa.status = 1
    WHERE p.status = 1 AND pa.number = parameterId;

    RETURN objectId;

END //

DELIMITER ;

INSERT INTO reward_parameter_attribute (domain_id, entity_attribute_id, number) VALUE (1, 6, selectParameter(1));
INSERT INTO reward_parameter_attribute (domain_id, entity_attribute_id, number) VALUE (2, 6, selectParameter(2));
INSERT INTO reward_parameter_attribute (domain_id, entity_attribute_id, number) VALUE (3, 6, selectParameter(3));
INSERT INTO reward_parameter_attribute (domain_id, entity_attribute_id, number) VALUE (4, 6, selectParameter(4));
INSERT INTO reward_parameter_attribute (domain_id, entity_attribute_id, number) VALUE (5, 6, selectParameter(5));
INSERT INTO reward_parameter_attribute (domain_id, entity_attribute_id, number) VALUE (6, 6, selectParameter(6));
INSERT INTO reward_parameter_attribute (domain_id, entity_attribute_id, number) VALUE (7, 6, selectParameter(7));
INSERT INTO reward_parameter_attribute (domain_id, entity_attribute_id, number) VALUE (8, 6, selectParameter(8));
INSERT INTO reward_parameter_attribute (domain_id, entity_attribute_id, number) VALUE (9, 6, selectParameter(9));
INSERT INTO reward_parameter_attribute (domain_id, entity_attribute_id, number) VALUE (10, 6, selectParameter(10));
INSERT INTO reward_parameter_attribute (domain_id, entity_attribute_id, number) VALUE (47, 6, selectParameter(11));
INSERT INTO reward_parameter_attribute (domain_id, entity_attribute_id, number) VALUE (11, 6, selectParameter(12));
INSERT INTO reward_parameter_attribute (domain_id, entity_attribute_id, number) VALUE (12, 6, selectParameter(13));
INSERT INTO reward_parameter_attribute (domain_id, entity_attribute_id, number) VALUE (13, 6, selectParameter(14));
INSERT INTO reward_parameter_attribute (domain_id, entity_attribute_id, number) VALUE (14, 6, selectParameter(15));
INSERT INTO reward_parameter_attribute (domain_id, entity_attribute_id, number) VALUE (15, 6, selectParameter(16));
INSERT INTO reward_parameter_attribute (domain_id, entity_attribute_id, number) VALUE (16, 6, selectParameter(17));
INSERT INTO reward_parameter_attribute (domain_id, entity_attribute_id, number) VALUE (17, 6, selectParameter(18));
INSERT INTO reward_parameter_attribute (domain_id, entity_attribute_id, number) VALUE (18, 6, selectParameter(19));
INSERT INTO reward_parameter_attribute (domain_id, entity_attribute_id, number) VALUE (19, 6, selectParameter(20));
INSERT INTO reward_parameter_attribute (domain_id, entity_attribute_id, number) VALUE (20, 6, selectParameter(21));
INSERT INTO reward_parameter_attribute (domain_id, entity_attribute_id, number) VALUE (48, 6, selectParameter(22));
INSERT INTO reward_parameter_attribute (domain_id, entity_attribute_id, number) VALUE (21, 6, selectParameter(23));
INSERT INTO reward_parameter_attribute (domain_id, entity_attribute_id, number) VALUE (22, 6, selectParameter(24));
INSERT INTO reward_parameter_attribute (domain_id, entity_attribute_id, number) VALUE (23, 6, selectParameter(25));
INSERT INTO reward_parameter_attribute (domain_id, entity_attribute_id, number) VALUE (24, 6, selectParameter(26));
INSERT INTO reward_parameter_attribute (domain_id, entity_attribute_id, number) VALUE (25, 6, selectParameter(27));
INSERT INTO reward_parameter_attribute (domain_id, entity_attribute_id, number) VALUE (26, 6, selectParameter(28));
INSERT INTO reward_parameter_attribute (domain_id, entity_attribute_id, number) VALUE (27, 6, selectParameter(29));
INSERT INTO reward_parameter_attribute (domain_id, entity_attribute_id, number) VALUE (28, 6, selectParameter(30));
INSERT INTO reward_parameter_attribute (domain_id, entity_attribute_id, number) VALUE (29, 6, selectParameter(31));
INSERT INTO reward_parameter_attribute (domain_id, entity_attribute_id, number) VALUE (30, 6, selectParameter(32));
INSERT INTO reward_parameter_attribute (domain_id, entity_attribute_id, number) VALUE (49, 6, selectParameter(33));
INSERT INTO reward_parameter_attribute (domain_id, entity_attribute_id, number) VALUE (31, 6, selectParameter(34));
INSERT INTO reward_parameter_attribute (domain_id, entity_attribute_id, number) VALUE (32, 6, selectParameter(35));
INSERT INTO reward_parameter_attribute (domain_id, entity_attribute_id, number) VALUE (33, 6, selectParameter(36));
INSERT INTO reward_parameter_attribute (domain_id, entity_attribute_id, number) VALUE (34, 6, selectParameter(37));
INSERT INTO reward_parameter_attribute (domain_id, entity_attribute_id, number) VALUE (35, 6, selectParameter(38));
INSERT INTO reward_parameter_attribute (domain_id, entity_attribute_id, number) VALUE (36, 6, selectParameter(39));
INSERT INTO reward_parameter_attribute (domain_id, entity_attribute_id, number) VALUE (37, 6, selectParameter(40));
INSERT INTO reward_parameter_attribute (domain_id, entity_attribute_id, number) VALUE (38, 6, selectParameter(41));
INSERT INTO reward_parameter_attribute (domain_id, entity_attribute_id, number) VALUE (39, 6, selectParameter(42));
INSERT INTO reward_parameter_attribute (domain_id, entity_attribute_id, number) VALUE (40, 6, selectParameter(43));
INSERT INTO reward_parameter_attribute (domain_id, entity_attribute_id, number) VALUE (41, 6, selectParameter(44));
INSERT INTO reward_parameter_attribute (domain_id, entity_attribute_id, number) VALUE (42, 6, selectParameter(45));
INSERT INTO reward_parameter_attribute (domain_id, entity_attribute_id, number) VALUE (43, 6, selectParameter(46));
INSERT INTO reward_parameter_attribute (domain_id, entity_attribute_id, number) VALUE (44, 6, selectParameter(47));
INSERT INTO reward_parameter_attribute (domain_id, entity_attribute_id, number) VALUE (45, 6, selectParameter(48));
INSERT INTO reward_parameter_attribute (domain_id, entity_attribute_id, number) VALUE (46, 6, selectParameter(49));
INSERT INTO reward_parameter_attribute (domain_id, entity_attribute_id, number) VALUE (50, 6, selectParameter(50));
INSERT INTO reward_parameter_attribute (domain_id, entity_attribute_id, number) VALUE (51, 6, selectParameter(51));
INSERT INTO reward_parameter_attribute (domain_id, entity_attribute_id, number) VALUE (52, 6, selectParameter(52));
INSERT INTO reward_parameter_attribute (domain_id, entity_attribute_id, number) VALUE (53, 6, selectParameter(53));
INSERT INTO reward_parameter_attribute (domain_id, entity_attribute_id, number) VALUE (54, 6, selectParameter(54));
INSERT INTO reward_parameter_attribute (domain_id, entity_attribute_id, number) VALUE (55, 6, selectParameter(55));
INSERT INTO reward_parameter_attribute (domain_id, entity_attribute_id, number) VALUE (56, 6, selectParameter(56));
INSERT INTO reward_parameter_attribute (domain_id, entity_attribute_id, number) VALUE (57, 6, selectParameter(57));
INSERT INTO reward_parameter_attribute (domain_id, entity_attribute_id, number) VALUE (58, 6, selectParameter(58));
INSERT INTO reward_parameter_attribute (domain_id, entity_attribute_id, number) VALUE (59, 6, selectParameter(59));
INSERT INTO reward_parameter_attribute (domain_id, entity_attribute_id, number) VALUE (60, 6, selectParameter(60));

DROP FUNCTION selectParameter;

INSERT INTO `update` (`version`) VALUE ('2.0.18');