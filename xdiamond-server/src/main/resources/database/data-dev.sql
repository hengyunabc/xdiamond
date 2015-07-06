
INSERT INTO `user` VALUES ('1', 'admin', 'admin', '8659c1f36193d2f8f062a4afe44c0d71f2a28d6997fe3207247f9485a8c5b5efa3c34f329e787be310cb31f4f1adc370057b538c4692838d3126cc544f53e369', 'djWHvtVYFI5pA5OviK1278joLYBXqnxM', 'admin@test.com', '2015-06-05 16:25:17', null, null, null, 'standard');

INSERT INTO `group` VALUES ('1', 'admin', 'admin');

INSERT INTO `user_groups` VALUES ('1', '1', '50');

INSERT INTO `permission` VALUES ('1', 'admin', 'admin');
INSERT INTO `permission` VALUES ('2', '*', 'admin');

INSERT INTO `role` VALUES ('1', 'admin', 'admin，管理员');

INSERT INTO `role_permissions` VALUES ('1', '1');
INSERT INTO `role_permissions` VALUES ('1', '2');

INSERT INTO `group_roles` VALUES ('1', '1');

-- ----------------------------
-- Records of project
-- ----------------------------
INSERT INTO `project` VALUES ('1', 'io.github.xdiamond', 'xdiamond-example-memcached', '0.0.1', '1', 'memcached配置', '1', '1');
INSERT INTO `project` VALUES ('2', 'io.github.xdiamond', 'xdiamond-example-zookeeper', '0.0.1', '1', 'zookeeper配置', '1', '1');
INSERT INTO `project` VALUES ('3', 'io.github.xdiamond', 'xdiamond-client-example', '0.0.1-SNAPSHOT', '1', 'xdiamond-client-example', '1', '0');

-- ----------------------------
-- Records of profile
-- ----------------------------
INSERT INTO `profile` VALUES ('1', '1', 'base', '40', null, null, null, null);
INSERT INTO `profile` VALUES ('2', '1', 'test', '30', null, null, null, null);
INSERT INTO `profile` VALUES ('3', '1', 'dev', '30', null, null, null, null);
INSERT INTO `profile` VALUES ('4', '1', 'product', '40', 'N8higEuXU8yoCIYV', null, null, null);
INSERT INTO `profile` VALUES ('5', '2', 'base', '40', null, null, null, null);
INSERT INTO `profile` VALUES ('6', '2', 'test', '30', null, null, null, null);
INSERT INTO `profile` VALUES ('7', '2', 'dev', '30', null, null, null, null);
INSERT INTO `profile` VALUES ('8', '2', 'product', '40', 'Cu9TdQ8qO1Kyjfyb', null, null, null);
INSERT INTO `profile` VALUES ('9', '3', 'base', '40', null, null, null, null);
INSERT INTO `profile` VALUES ('10', '3', 'test', '30', null, null, null, null);
INSERT INTO `profile` VALUES ('11', '3', 'dev', '30', null, null, null, null);
INSERT INTO `profile` VALUES ('12', '3', 'product', '40', 'b8ylj4r0OcBMgdNU', null, null, null);

-- ----------------------------
-- Records of dependency
-- ----------------------------
INSERT INTO `dependency` VALUES ('1', '3', '1');
INSERT INTO `dependency` VALUES ('2', '3', '2');

-- ----------------------------
-- Records of config
-- ----------------------------
INSERT INTO `config` VALUES ('1', '3', 'memcached.serverlist', 'localhost:11211', null, null, '2015-07-06 15:01:56', null, 'admin', null, '0');
INSERT INTO `config` VALUES ('2', '4', 'memcached.serverlist', '192.168.90.147:51211 192.168.90.147:61211', null, null, '2015-07-06 15:02:43', null, 'admin', null, '0');
INSERT INTO `config` VALUES ('3', '5', 'zookeeper.address', 'localhost:2181', null, null, '2015-07-06 15:04:23', null, 'admin', null, '0');
INSERT INTO `config` VALUES ('4', '8', 'zookeeper.address', '192.168.90.147:2181,192.168.90.147:3181,192.168.90.147:4181', null, null, '2015-07-06 15:04:46', null, 'admin', null, '0');
INSERT INTO `config` VALUES ('5', '1', 'memcached.serverlist', 'localhost:11211', null, null, '2015-07-06 15:05:37', null, 'admin', null, '0');


