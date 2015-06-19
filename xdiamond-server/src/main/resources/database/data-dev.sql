
INSERT INTO `user` VALUES ('1', 'admin', 'admin', '8659c1f36193d2f8f062a4afe44c0d71f2a28d6997fe3207247f9485a8c5b5efa3c34f329e787be310cb31f4f1adc370057b538c4692838d3126cc544f53e369', 'djWHvtVYFI5pA5OviK1278joLYBXqnxM', 'admin@test.com', '2015-06-05 16:25:17', null, null, null, 'standard');

INSERT INTO `group` VALUES ('1', 'admin', 'admin');

INSERT INTO `user_groups` VALUES ('1', '1', '50');

INSERT INTO `permission` VALUES ('1', 'admin', 'admin');
INSERT INTO `permission` VALUES ('2', '*', 'admin');

INSERT INTO `role` VALUES ('1', 'admin', 'admin，管理员');

INSERT INTO `role_permissions` VALUES ('1', '1');
INSERT INTO `role_permissions` VALUES ('1', '2');

INSERT INTO `group_roles` VALUES ('1', '1');
