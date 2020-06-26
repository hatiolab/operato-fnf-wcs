SELECT
	:batchId AS BATCH_ID
	, :orderNo AS ORDER_NO
	, :wmsBatchNo AS WMS_BATCH_NO
	, :wcsBatchNo AS WCS_BATCH_NO
	, :jobDate AS JOB_DATE
	, SEQ AS JOB_SEQ
	, 'SRTN' AS JOB_TYPE
	, :orderDate AS ORDER_DATE
	, 'FnF' AS COM_CD
	, 'A' AS AREA_CD
	, 'AB1' AS STAGE_CD
	, 'SORTER' AS EQUIP_TYPE
	, ITEM_CD AS SKU_CD
	, BARCODE1 AS SKU_BARCD
	, BARCODE2 AS SKU_BARCD2
	, EXP_QTY AS ORDER_QTY
FROM
	WCS_RTN_CHASU_SKU
WHERE
	STRR_ID || '-' || SEASON || '-' || TYPE || '-' || SEQ = :wmsBatchNo