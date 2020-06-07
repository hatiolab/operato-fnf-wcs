SELECT
	H.DESCR AS BATCH_TITLE,
	H.WORK_UNIT AS BATCH_ID,
	D.STRR_NM AS BRAND,
	D.REF_NO AS ORDER_NO,
	D.WAYBILL_NO AS INVOICE_ID,
	D.BOX_NO AS TRAY_CD,
	D.BOX_ID AS BOX_ID,
	D.STATUS,
	COUNT(DISTINCT(D.ITEM_CD)) AS SKU_QTY,
	COALESCE(SUM(D.CMPT_QTY), 0) AS PICKED_QTY
FROM
	MHE_HR H INNER JOIN MHE_DR D ON H.WH_CD = D.WH_CD AND H.WORK_UNIT = D.WORK_UNIT
WHERE
	H.WH_CD = 'ICF'
	AND H.WORK_UNIT = :batchId
	#if($orderNo)
	AND D.REF_NO = :orderNo
	#end
	#if($invoiceId)
	AND D.WAYBILL_NO = :invoiceId
	#end
	#if($boxId)
	AND (D.BOX_NO = :boxId OR D.BOX_ID = :boxId)
	#end
	#if($status)
	AND D.STATUS = :status
	#end
GROUP BY
	H.DESCR, H.WORK_UNIT, D.STRR_NM, D.REF_NO, D.WAYBILL_NO, D.BOX_NO, D.BOX_ID, D.STATUS