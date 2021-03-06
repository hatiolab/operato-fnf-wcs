SELECT
	SUB_EQUIP_CD AS CHUTE_NO
	, CELL_ASSGN_CD AS SHOP_CD 
	, CELL_ASSGN_NM AS SHOP_NM
	, SUM(TOTAL_PCS) AS ASSIGNED_PCS
FROM
	ORDER_PREPROCESSES OP
WHERE
      DOMAIN_ID = :domainId
AND   BATCH_ID  = :batchId
GROUP BY
	SUB_EQUIP_CD
	, CELL_ASSGN_CD
	, CELL_ASSGN_NM
ORDER BY 
	CHUTE_NO