INSERT INTO `locale`(`id`, `locale`, `system`) VALUES (1, 'RU', 1);
INSERT INTO `locale`(`id`, `locale`, `system`) VALUES (2, 'UA', 0);

INSERT INTO `sequence` (`name`) VALUES ('city'), ('city_type'), ('region'), ('profile');

INSERT INTO entity_value_type (id, value_type) VALUE (0, 'string_value');
INSERT INTO entity_value_type (id, value_type) VALUE (1, 'string');
INSERT INTO entity_value_type (id, value_type) VALUE (2, 'boolean');
INSERT INTO entity_value_type (id, value_type) VALUE (3, 'decimal');
INSERT INTO entity_value_type (id, value_type) VALUE (4, 'integer');
INSERT INTO entity_value_type (id, value_type) VALUE (5, 'date');
INSERT INTO entity_value_type (id, value_type) VALUE (10, 'entity');

INSERT INTO `entity`(`id`, `name`) VALUES (2, 'region');
INSERT INTO `entity_value`(`entity_id`, `locale_id`, `text`) VALUES (2, 1, 'Регион'), (2, 2, 'Регіон');
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (2, 1, 0);
INSERT INTO `entity_value`(`entity_id`, `attribute_id`, `locale_id`, `text`) VALUES (2, 1, 1, 'Краткое название'), (2, 1, 2, 'Коротка назва');
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (2, 2, 0);
INSERT INTO `entity_value`(`entity_id`, `attribute_id`, `locale_id`, `text`) VALUES (2, 2, 1, 'Название'), (2, 2, 2, 'Назва');
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (2, 3, 4);
INSERT INTO `entity_value`(`entity_id`, `attribute_id`, `locale_id`, `text`) VALUES (2, 3, 1, 'Менеджер'), (2, 3, 2, 'Менеджер');

INSERT INTO `entity`(`id`, `name`) VALUES (3, 'city_type');
INSERT INTO `entity_value`(`entity_id`, `locale_id`, `text`) VALUES (3, 1, 'Тип населенного пункта'), (3, 2, 'Тип населеного пункту');
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (3, 1, 0);
INSERT INTO `entity_value`(`entity_id`, `attribute_id`, `locale_id`, `text`) VALUES (3, 1, 1, 'Краткое название'), (3, 1, 2, 'Коротка назва');
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`,  `value_type_id`) VALUES (3, 2, 0);
INSERT INTO `entity_value`(`entity_id`, `attribute_id`, `locale_id`, `text`) VALUES (3, 2, 1, 'Название'), (3, 2, 2, 'Назва');

INSERT INTO `entity`(`id`, `name`) VALUES (4, 'city');
INSERT INTO `entity_value`(`entity_id`, `locale_id`, `text`) VALUES (4, 1, 'Населенный пункт'), (4, 2, 'Населений пункт');
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (4, 1, 0);
INSERT INTO `entity_value`(`entity_id`, `attribute_id`, `locale_id`, `text`) VALUES (4, 1, 1, 'Краткое название'), (4, 1, 2, 'Коротка назва');
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`,  `value_type_id`) VALUES (4, 2, 0);
INSERT INTO `entity_value`(`entity_id`, `attribute_id`, `locale_id`, `text`) VALUES (4, 2, 1, 'Название'), (4, 2, 2, 'Назва');
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (4, 3, 4);
INSERT INTO `entity_value`(`entity_id`, `attribute_id`, `locale_id`, `text`) VALUES (4, 3, 1, 'Менеджер'), (4, 3, 2, 'Менеджер');


-- todo user attributes
INSERT INTO `entity`(`id`, `name`) VALUES (10, 'profile');
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (10, 1, 1);
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (10, 2, 1);
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (10, 3, 1);
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (10, 4, 1);
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (10, 5, 5);
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (10, 6, 5);
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (10, 7, 5);
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (10, 8, 5);
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (10, 9, 4);
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (10, 10, 0);
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (10, 11, 0);
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (10, 12, 0);
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (10, 13, 1);
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`, `reference_id`) VALUES (10, 14, 10, 4);
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (10, 15, 4);
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (10, 16, 5);
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (10, 17, 1);
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (10, 18, 1);
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (10, 19, 4);
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (10, 20, 1);
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (10, 21, 4);
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (10, 22, 2);
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (10, 23, 4);
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (10, 24, 1);

INSERT INTO `entity`(`id`, `name`) VALUES (11, 'user');




