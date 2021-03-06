SELECT 
	B.BATCH_ID,
	B.INPUT_SEQ,
	(SELECT EQUIP_TYPE FROM JOB_BATCHES WHERE DOMAIN_ID = :domainId AND ID = :batchId) AS EQUIP_TYPE,
	(SELECT EQUIP_CD FROM JOB_BATCHES WHERE DOMAIN_ID = :domainId AND ID = :batchId) AS EQUIP_CD,
	B.ORDER_NO,
	B.BOX_ID,
	'A' AS BOX_TYPE,
	B.SKU_QTY,
	B.PLAN_QTY,
	B.RESULT_QTY,
	CASE WHEN B.PLAN_QTY = B.RESULT_QTY THEN '1' ELSE '0' END AS STATUS,
	B.COLOR_CD
  FROM (
	SELECT
		A.BATCH_ID,
		MIN(A.INPUT_SEQ) AS INPUT_SEQ,
		A.ORDER_NO,
		A.BOX_ID,
		COUNT(1) AS SKU_QTY,
		SUM(A.PICK_QTY) AS PLAN_QTY,
		COALESCE(SUM(A.PICKED_QTY), 0) AS RESULT_QTY,
		MAX(A.COLOR_CD) AS COLOR_CD
	FROM (
		SELECT 
			WORK_UNIT AS BATCH_ID,
			REF_NO AS ORDER_NO,
			MIN(BOX_INPUT_SEQ) AS INPUT_SEQ,
			BOX_NO AS BOX_ID,
			'FnF' AS COM_CD,
			ITEM_CD AS SKU_CD,
			SUM(PICK_QTY) AS PICK_QTY,
			COALESCE(SUM(CMPT_QTY), 0) AS PICKED_QTY,
			'Y' AS COLOR_CD
		FROM
			DPS_JOB_INSTANCES
		WHERE
			WORK_UNIT = :batchId
			AND BOX_INPUT_SEQ > 0
		GROUP BY
			WORK_UNIT, REF_NO, BOX_NO, ITEM_CD) A
	GROUP BY
		BATCH_ID, ORDER_NO, BOX_ID
	) B
 ORDER BY
 	B.INPUT_SEQ DESC