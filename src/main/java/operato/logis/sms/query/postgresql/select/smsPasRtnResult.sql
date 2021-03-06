select 
	mpr.batch_no as batch_id, mpr.job_type, mpr.chute_no, mpr.sku_cd, mpr.sku_bcd, mpr.strr_id
	, mpo.order_qty, coalesce(mpr.qty, 0) as qty, coalesce(mdrbr.cmpt_qty, 0) as cmpt_qty
	, coalesce(mpr.qty, 0) - coalesce(mdrbr.cmpt_qty, 0) as diff_qty
from 
	(select batch_no, chute_no, sku_cd, sum(order_qty) order_qty from mhe_pas_order 
	where batch_no in ( :batchList ) group by batch_no, chute_no, sku_cd) as mpo
left outer join 
	(select batch_no, job_type, chute_no, sku_cd, sku_bcd, strr_id, sum(qty) qty from mhe_pas_rlst 
	where batch_no in ( :batchList ) group by batch_no, job_type, chute_no, sku_cd, sku_bcd, strr_id) as mpr
on
	mpo.batch_no = mpr.batch_no
and
	mpo.chute_no = mpr.chute_no
and
	mpo.sku_cd = mpr.sku_cd
left outer join 
	(
		select 
			batch_no, item_cd, strr_id, sum(cmpt_qty) cmpt_qty 
		from 
			mhe_das_rtn_box_rslt 
		where 
			batch_no in ( :batchList )
		group by 
			batch_no, item_cd, strr_id
	) mdrbr
on
	mpr.batch_no = mdrbr.batch_no
and
	mpr.strr_id = mdrbr.strr_id
and
	mpr.sku_cd = mdrbr.item_cd
where 
	mpr.batch_no in ( :batchList )
order by 
	mpr.chute_no, mpr.sku_cd