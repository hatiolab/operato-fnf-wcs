SELECT
	 MDO.BATCH_NO AS BATCH_ID
	 , JB.TITLE AS TITLE
	 , MDRR.BOX_NO
	 , MDO.CHUTE_NO
	 , MDO.CELL_NO
	 , MDRR.ITEM_CD AS SKU_CD
	 , SKU.SKU_NM AS SKU_NM
	 , MDRR.CMPT_QTY AS PICKED_QTY
	 , MDO.PLT_NO
FROM
	MHE_DAS_RTN_BOX_RSLT MDRR
LEFT OUTER JOIN
	MHE_DAS_ORDER MDO
ON
	MDRR.BATCH_NO = MDO.BATCH_NO
AND
	MDRR.ITEM_CD = MDO.ITEM_CD
LEFT OUTER JOIN
	JOB_BATCHES JB
ON
	MDRR.BATCH_NO = JB.ID
LEFT OUTER JOIN
	SKU SKU
ON
	MDRR.ITEM_CD = SKU.SKU_CD
AND
	SKU.DOMAIN_ID = :domainId
AND
	SKU.COM_CD = :comCd
WHERE
	MDRR.BATCH_NO = :batch_id