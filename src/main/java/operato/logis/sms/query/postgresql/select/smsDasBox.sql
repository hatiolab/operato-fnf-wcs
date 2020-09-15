SELECT
	MDO.JOB_DATE
	, MB.WORK_UNIT AS BATCH_ID
	, JB.TITLE
	, MDO.CHUTE_NO
	, MDO.CELL_NO
	, MB.BOX_NO
	, MDO.ITEM_CD AS SKU_CD
	, SKU.SKU_BARCD
	, SKU.BRAND_CD
	, SKU.SEASON_CD
	, SKU.STYLE_CD
	, SKU.COLOR_CD
	, SKU.SIZE_CD
	, MB.CMPT_QTY AS PICKED_QTY
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
LEFT OUTER JOIN
	JOB_BATCHES JB
ON
	MDO.BATCH_NO = JB.ID
WHERE
	MB.WORK_UNIT IN ( :batchList )
#if($cell_no)
AND MDO.CELL_NO LIKE :cell_no
#end
#if($box_no)
AND MB.BOX_NO LIKE :box_no
#end
#if($sku_cd)
AND MDO.ITEM_CD LIKE :sku_cd
#end
ORDER BY
	MB.WORK_UNIT, MDO.CHUTE_NO, MDO.CELL_NO