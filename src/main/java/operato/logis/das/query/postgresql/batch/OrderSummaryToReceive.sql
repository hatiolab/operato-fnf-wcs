SELECT 
	 'FnF' AS COM_CD,
	 'A' AS AREA_CD,
	 '' AS STAGE_CD,
	 'DAS' AS JOB_TYPE,
	 HR.work_unit AS WMS_BATCH_NO,
	 HR.workseq_no AS JOB_SEQ,
	 HR.descr AS MESSAGE,
	 HR.strr_id AS BRAND_CD,
	 (SELECT COUNT(*) FROM MHE_DR WHERE WH_CD = HR.WH_CD AND WORK_UNIT = HR.WORK_UNIT) AS TOTAL_RECORDS,
	 (SELECT COUNT(DISTINCT(REF_NO)) FROM MHE_DR WHERE WH_CD = HR.WH_CD AND WORK_UNIT = HR.WORK_UNIT) AS TOTAL_ORDERS,
	 (SELECT SUM(PICK_QTY) FROM MHE_DR WHERE WH_CD = HR.WH_CD AND WORK_UNIT = HR.WORK_UNIT) AS TOTAL_PCS,
	 'Rack' AS EQUIP_TYPE,
	 'order' AS ITEM_TYPE,
	 'W' AS STATUS,
	 0 AS SKIP_FLAG
FROM 
  	 MHE_HR HR
WHERE 
 	 HR.WH_CD = :whCd
 	 AND HR.WORK_DATE = :jobDate
 	 AND HR.STATUS = :status
 	 AND HR.BIZ_TYPE = :jobType