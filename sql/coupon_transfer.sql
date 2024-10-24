UPDATE coupon SET apply_scene = 1 WHERE type IN (0, 100);

UPDATE coupon_possess
SET apply_scene = (SELECT apply_scene FROM coupon WHERE coupon.id = coupon_possess.coupon_id);