-- Update positions

UPDATE position SET status = 2, end_date = now() WHERE id = 2;

UPDATE position_value SET text = 'РУКОВОДИТЕЛЬ РЕГИОНА' where id = 3;

INSERT INTO position (`id`, `object_id`, `start_date`, `status`, `user_id`) VALUE (4, 4, now(), 1, 1);
INSERT INTO position_attribute (`id`, `object_id`, `entity_attribute_id`, `start_date`, `status`, `user_id`) VALUE (4, 4, 1, now(), 1, 1);
INSERT INTO position_value (`id`, `attribute_id`, `locale_id`, text) VALUE (4, 4, 1, 'ТОРГОВЫЙ ДИРЕКТОР');

INSERT INTO position (`id`, `object_id`, `start_date`, `status`, `user_id`) VALUE (5, 5, now(), 1, 1);
INSERT INTO position_attribute (`id`, `object_id`, `entity_attribute_id`, `start_date`, `status`, `user_id`) VALUE (5, 5, 1, now(), 1, 1);
INSERT INTO position_value (`id`, `attribute_id`, `locale_id`, text) VALUE (5, 5, 1, 'АДМИНИСТРАТИВНЫЙ ДИРЕКТОР');

-- Update

INSERT INTO `update` (`version`) VALUE ('20190416_1.0.12');