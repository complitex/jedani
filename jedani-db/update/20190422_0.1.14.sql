ALTER TABLE `price` DROP KEY `unique_object_id__status`;

-- Update

INSERT INTO `update` (`version`) VALUE ('20190422_1.0.14');