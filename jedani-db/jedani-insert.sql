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
INSERT INTO `entity_value`(`entity_id`, `attribute_id`, `locale_id`, `text`) VALUES (2, 1, 1, 'Название'), (2, 1, 2, 'Назва');
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (2, 2, 0);
INSERT INTO `entity_value`(`entity_id`, `attribute_id`, `locale_id`, `text`) VALUES (2, 2, 1, 'Краткое название'), (2, 2, 2, 'Коротка назва');
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (2, 3, 4);
INSERT INTO `entity_value`(`entity_id`, `attribute_id`, `locale_id`, `text`) VALUES (2, 3, 1, 'Менеджер'), (2, 3, 2, 'Менеджер');

INSERT INTO `entity`(`id`, `name`) VALUES (3, 'city_type');
INSERT INTO `entity_value`(`entity_id`, `locale_id`, `text`) VALUES (3, 1, 'Тип населенного пункта'), (3, 2, 'Тип населеного пункту');
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (3, 1, 0);
INSERT INTO `entity_value`(`entity_id`, `attribute_id`, `locale_id`, `text`) VALUES (3, 1, 1, 'Название'), (3, 1, 2, 'Назва');
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (3, 2, 0);
INSERT INTO `entity_value`(`entity_id`, `attribute_id`, `locale_id`, `text`) VALUES (3, 2, 1, 'Краткое название'), (3, 2, 2, 'Коротка назва');

INSERT INTO `entity`(`id`, `name`) VALUES (4, 'city');
INSERT INTO `entity_value`(`entity_id`, `locale_id`, `text`) VALUES (4, 1, 'Населенный пункт'), (4, 2, 'Населений пункт');
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (4, 1, 0);
INSERT INTO `entity_value`(`entity_id`, `attribute_id`, `locale_id`, `text`) VALUES (4, 1, 1, 'Название'), (4, 1, 2, 'Назва');
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (4, 2, 0);
INSERT INTO `entity_value`(`entity_id`, `attribute_id`, `locale_id`, `text`) VALUES (4, 2, 1, 'Краткое название'), (4, 2, 2, 'Коротка назва');
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (4, 3, 4);
INSERT INTO `entity_value`(`entity_id`, `attribute_id`, `locale_id`, `text`) VALUES (4, 3, 1, 'Менеджер'), (4, 3, 2, 'Менеджер');

INSERT INTO `entity`(`id`, `name`) VALUES (10, 'user');

-- todo user attributes
INSERT INTO `entity`(`id`, `name`) VALUES (11, 'profile');
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (11, 1, 1);
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (11, 2, 1);
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (11, 3, 1);
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (11, 4, 1);
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (11, 5, 5);
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (11, 6, 5);
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (11, 7, 5);
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (11, 8, 5);
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (11, 9, 4);
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (11, 10, 0);
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (11, 11, 0);
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (11, 12, 0);
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (11, 13, 1);
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`, `reference_id`) VALUES (11, 14, 10, 4);
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (11, 15, 4);
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (11, 16, 5);
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (11, 17, 1);
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (11, 18, 1);
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (11, 19, 4);
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (11, 20, 1);
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (11, 21, 4);
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (11, 22, 2);
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (11, 23, 4);
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (11, 24, 1);






