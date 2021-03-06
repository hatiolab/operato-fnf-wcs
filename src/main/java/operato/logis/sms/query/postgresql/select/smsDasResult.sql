SELECT
	 MB.WORK_UNIT AS BATCH_ID
	 , MDO.JOB_DATE
	 , MDO.CHUTE_NO
	 , MDO.SHOP_CD
	 , MDO.SHOP_NM
	 , MDO.CELL_NO
	 , MDO.ITEM_CD AS SKU_CD
	 , SKU.SKU_NM AS SKU_NM
	 , MDO.ORDER_QTY
	 , MB.CMPT_QTY AS PICKED_QTY
	 , MB.BOX_NO
FROM
	MHE_BOX MB
LEFT OUTER JOIN
	MHE_DAS_ORDER MDO
ON
	MB.WORK_UNIT = MDO.BATCH_NO
AND
	MB.SHIPTO_ID = MDO.SHOP_CD
AND
	MB.ITEM_CD = MDO.ITEM_CD
LEFT OUTER JOIN
	SKU SKU
ON
	MB.ITEM_CD = SKU.SKU_CD
AND
	SKU.DOMAIN_ID = :domainId
AND
	SKU.COM_CD = :comCd
WHERE
	MB.WORK_UNIT = :batch_id