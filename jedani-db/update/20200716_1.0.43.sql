DELETE sv FROM storage_value sv
    LEFT JOIN storage_attribute sa ON sv.attribute_id = sa.id
    LEFT JOIN storage s on sa.domain_id = s.id
WHERE s.parent_id IS NOT NULL;

DELETE sa FROM storage_attribute sa
    LEFT JOIN storage s on sa.domain_id = s.id
WHERE s.parent_id IS NOT NULL;

DELETE FROM storage s WHERE s.parent_id IS NOT NULL;

-- Update

INSERT INTO `update` (`version`) VALUE ('20200716_1.0.43');
