CREATE TABLE `user_history` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор',
  `user_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор пользователя',
  `login` VARCHAR(64) COMMENT 'Логин',
  `password` VARCHAR(64) COMMENT 'Пароль',
  `group` VARCHAR(512) COMMENT 'Группы пользователя',
  `date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата обновления',
  `worker_id` BIGINT(20) COMMENT 'Идентификатор сотрудника',
  PRIMARY KEY (`id`),
  KEY `key_user_id` (`user_id`),
  KEY `key_login` (`login`),
  KEY `key_group`(`group`(128)),
  KEY `key_date` (`date`),
  KEY `key_worker_id` (`worker_id`),
  CONSTRAINT `fk_user_history__user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `fk_user_history__worker` FOREIGN KEY (`worker_id`) REFERENCES `worker` (`object_id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'История пользователя';

-- Update

INSERT INTO `update` (`version`) VALUE ('20190725_1.0.24');