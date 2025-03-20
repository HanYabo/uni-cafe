-- 添加软删除字段到orders表
ALTER TABLE orders ADD COLUMN is_deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除 1-已删除';

-- 添加索引，提高查询性能
CREATE INDEX idx_orders_is_deleted ON orders(is_deleted);

-- 更新现有记录为未删除状态
UPDATE orders SET is_deleted = 0 WHERE is_deleted IS NULL; 